package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.user.dto.DictQueryDTO;
import com.jycloud.api.user.entity.Dict;
import com.jycloud.api.user.vo.DictVO;
import com.jycloud.service.user.repository.mapper.DictMapper;
import com.jycloud.service.user.service.DictService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 顶级节点父主键（parentId=0 表示字典类型）
     */
    private static final long TOP_PARENT_ID = 0L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Dict create(Dict dict) {
        if (dict == null || StringUtils.isBlank(dict.getDictValue())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        dict.setDictId(null);
        long parentId = dict.getParentId() == null ? TOP_PARENT_ID : dict.getParentId();

        if (parentId == TOP_PARENT_ID) {
            // 顶级：字典类型
            if (StringUtils.isBlank(dict.getCode())) {
                throw new SysException("字典码不能为空");
            }
            if (checkCode(dict.getCode())) {
                throw new SysException("字典码已存在");
            }
            dict.setParentId(TOP_PARENT_ID);
            dict.setDictKey(null);
        } else {
            // 子级：字典项
            Dict parent = getDictOrThrow(parentId);

            if (StringUtils.isBlank(dict.getDictKey())) {
                throw new SysException("字典值不能为空");
            }
            // code 继承父类型
            dict.setCode(parent.getCode());
            if (checkDictKey(parent.getCode(), dict.getDictKey())) {
                throw new SysException("该类型下字典值已存在");
            }
        }

        this.save(dict);
        return dict;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Dict update(Dict dict) {
        if (dict == null || dict.getDictId() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        Dict dbDict = getDictOrThrow(dict.getDictId());

        // 字典项：不允许变更 code（继承父类型）、不允许移动挂载点
        dict.setCode(null);
        dict.setParentId(null);
        if (StringUtils.isNotBlank(dict.getDictKey()) && !dict.getDictKey().equals(dbDict.getDictKey())) {
            if (checkDictKey(dbDict.getCode(), dict.getDictKey(), dict.getDictId())) {
                throw new SysException("该类型下字典值已存在");
            }
        }

        this.updateById(dict);
        return this.getById(dict.getDictId());
    }

    @Override
    public IPage<DictVO> pageByCondition(DictQueryDTO queryDTO) {
        Page<Dict> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "sort", SortUtil.ORDER_ASC, true);

        Dict entity = DtoEntityUtil.trans(queryDTO, Dict.class);
        entity.setParentId(TOP_PARENT_ID);
        LambdaQueryWrapper<Dict> qw = new LambdaQueryWrapper<>(entity);

        IPage<DictVO> voPage = this.page(page, qw).convert(dict -> DtoEntityUtil.trans(dict, DictVO.class));
        // 为分页返回的类型节点补充子级数据（多层级递归）
        fillChildren(voPage.getRecords());
        return voPage;
    }

    @Override
    public Dict getDictById(Long dictId) {
        if (dictId == null) {
            return null;
        }
        return this.getById(dictId);
    }

    @Override
    public List<DictVO> listDictTree() {
        List<Dict> dicts = this.list(new LambdaQueryWrapper<Dict>()
                .orderByAsc(Dict::getSort)
                .orderByAsc(Dict::getDictId));
        if (CollectionUtils.isEmpty(dicts)) {
            return new ArrayList<>();
        }

        Map<Long, List<DictVO>> group = dicts.stream()
                .map(dict -> DtoEntityUtil.trans(dict, DictVO.class))
                .collect(Collectors.groupingBy(
                        vo -> vo.getParentId() == null ? TOP_PARENT_ID : vo.getParentId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<DictVO> roots = group.getOrDefault(TOP_PARENT_ID, new ArrayList<>());
        roots.forEach(root -> buildChildren(root, group));
        return roots;
    }

    @Override
    public List<DictVO> listItemsByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return new ArrayList<>();
        }
        List<Dict> dicts = this.list(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getCode, code)
                .ne(Dict::getParentId, TOP_PARENT_ID)
                .eq(Dict::getStatus, 1)
                .orderByAsc(Dict::getSort)
                .orderByAsc(Dict::getDictId));
        return dicts.stream()
                .map(dict -> DtoEntityUtil.trans(dict, DictVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DictVO getDictByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        // 通过 code 一次查询所有数据（类型节点 + 各层级子项）
        List<Dict> dicts = this.list(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getCode, code)
                .orderByAsc(Dict::getSort)
                .orderByAsc(Dict::getDictId));
        if (CollectionUtils.isEmpty(dicts)) {
            return null;
        }

        // 转 VO 并按 parentId 分组，用于递归构建多级树
        Map<Long, List<DictVO>> group = dicts.stream()
                .map(dict -> DtoEntityUtil.trans(dict, DictVO.class))
                .collect(Collectors.groupingBy(
                        vo -> vo.getParentId() == null ? TOP_PARENT_ID : vo.getParentId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        // 根节点（parentId=0）递归挂载所有层级子节点
        List<DictVO> roots = group.getOrDefault(TOP_PARENT_ID, new ArrayList<>());
        roots.forEach(root -> buildChildren(root, group));
        if (CollectionUtils.isEmpty(roots)) {
            return null;
        }
        return roots.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDict(Long dictId) {
        if (dictId == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        getDictOrThrow(dictId);

        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;

        // 递归收集所有后代节点，先删子再删父，避免多层级下产生孤儿数据
        List<Long> childIds = new ArrayList<>();
        collectChildIds(dictId, childIds);
        for (Long childId : childIds) {
            this.baseMapper.deleteById(childId, deleteUid);
        }
        this.baseMapper.deleteById(dictId, deleteUid);
    }

    // ======================== 私有方法 ========================

    /**
     * 检查字典码是否已存在
     */
    private boolean checkCode(String code) {
        return checkCode(code, null);
    }

    /**
     * 检查字典码是否已存在（排除指定ID，用于更新）
     */
    private boolean checkCode(String code, Long dictId) {
        LambdaQueryWrapper<Dict> qw = new LambdaQueryWrapper<>();
        qw.eq(Dict::getCode, code)
                .eq(Dict::getParentId, TOP_PARENT_ID);
        if (dictId != null) {
            qw.ne(Dict::getDictId, dictId);
        }
        return this.count(qw) > 0;
    }

    /**
     * 检查同类型下字典值是否已存在
     */
    private boolean checkDictKey(String code, String dictKey) {
        return checkDictKey(code, dictKey, null);
    }

    /**
     * 检查同类型下字典值是否已存在（排除指定ID，用于更新）
     */
    private boolean checkDictKey(String code, String dictKey, Long dictId) {
        LambdaQueryWrapper<Dict> qw = new LambdaQueryWrapper<>();
        qw.eq(Dict::getCode, code)
                .eq(Dict::getDictKey, dictKey)
                .ne(Dict::getParentId, TOP_PARENT_ID);
        if (dictId != null) {
            qw.ne(Dict::getDictId, dictId);
        }
        return this.count(qw) > 0;
    }

    /**
     * 根据 ID 查询字典，不存在则抛出异常
     */
    private Dict getDictOrThrow(Long dictId) {
        Dict dict = this.getById(dictId);
        if (dict == null) {
            throw new SysException(ResponseCode.CHECK_NO_DATA);
        }
        return dict;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildren(DictVO parent, Map<Long, List<DictVO>> group) {
        List<DictVO> children = group.get(parent.getDictId());
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        parent.setChildren(children);
        children.forEach(child -> buildChildren(child, group));
    }

    /**
     * 为类型节点列表批量补充其所有层级子节点
     */
    private void fillChildren(List<DictVO> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }
        // 收集类型节点 code，一次查出其所有层级子节点（子节点 code 与父相同）
        List<String> codes = voList.stream()
                .map(DictVO::getCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        List<Dict> all = this.list(new LambdaQueryWrapper<Dict>()
                .in(Dict::getCode, codes)
                .orderByAsc(Dict::getSort)
                .orderByAsc(Dict::getDictId));
        if (CollectionUtils.isEmpty(all)) {
            return;
        }

        // 按 parentId 分组，对每个类型节点递归挂载子节点
        Map<Long, List<DictVO>> group = all.stream()
                .map(dict -> DtoEntityUtil.trans(dict, DictVO.class))
                .collect(Collectors.groupingBy(
                        vo -> vo.getParentId() == null ? TOP_PARENT_ID : vo.getParentId(),
                        LinkedHashMap::new,
                        Collectors.toList()));
        voList.forEach(vo -> buildChildren(vo, group));
    }

    /**
     * 递归收集指定节点下所有后代节点 ID（多层级级联删除用）
     */
    private void collectChildIds(Long parentId, List<Long> ids) {
        List<Dict> children = this.list(new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, parentId));
        for (Dict child : children) {
            ids.add(child.getDictId());
            collectChildIds(child.getDictId(), ids);
        }
    }
}
