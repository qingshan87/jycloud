package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jycloud.api.user.entity.Dept;
import com.jycloud.api.user.vo.DeptVO;
import com.jycloud.service.user.repository.mapper.DeptMapper;
import com.jycloud.service.user.service.DeptService;
import com.jycloud.service.user.service.UserDeptService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.DtoEntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 部门表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    private static final long TOP_PARENT_ID = 0L;

    private final UserDeptService userDeptService;

    @Override
    public List<DeptVO> listDeptTree(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return new ArrayList<>();
        }
        List<Dept> depts = this.list(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getTenantId, tenantId)
                .orderByAsc(Dept::getSort));
        if (CollectionUtils.isEmpty(depts)) {
            return new ArrayList<>();
        }

        Map<Long, List<DeptVO>> group = depts.stream()
                .map(dept -> DtoEntityUtil.trans(dept, DeptVO.class))
                .collect(Collectors.groupingBy(
                        vo -> vo.getParentId() == null ? TOP_PARENT_ID : vo.getParentId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<DeptVO> roots = group.getOrDefault(TOP_PARENT_ID, new ArrayList<>());
        roots.forEach(root -> buildChildren(root, group));
        return roots;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Dept createDept(Dept dept) {
        if (dept == null || StringUtils.isBlank(dept.getDeptName())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new SysException("租户ID不能为空");
        }

        fillDefault(dept);
        validateParent(dept.getParentId(), null);
        // 同级部门名唯一
        if (checkDeptName(tenantId, dept.getParentId(), dept.getDeptName())) {
            throw new SysException("同级下已存在同名部门");
        }

        dept.setTenantId(tenantId);
        this.save(dept);
        return dept;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Dept updateDept(Dept dept) {
        if (dept == null || dept.getDeptId() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        Dept oldDept = getDeptOrThrow(dept.getDeptId());
        if (dept.getParentId() == null) {
            dept.setParentId(TOP_PARENT_ID);
        }
        // 不允许设置自身为上级
        if (dept.getDeptId().equals(dept.getParentId())) {
            throw new SysException("上级部门不能是自身");
        }
        validateParent(dept.getParentId(), dept.getDeptId());
        // 同级部门名唯一（排除自身）
        if (StringUtils.isNotBlank(dept.getDeptName())
                && checkDeptName(oldDept.getTenantId(), dept.getParentId(), dept.getDeptName(), dept.getDeptId())) {
            throw new SysException("同级下已存在同名部门");
        }

        dept.setTenantId(null);
        this.updateById(dept);
        return this.getById(dept.getDeptId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long deptId) {
        if (deptId == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        Dept dept = getDeptOrThrow(deptId);

        // 存在子部门时拒绝删除
        long childCount = this.count(new LambdaQueryWrapper<Dept>().eq(Dept::getParentId, deptId));
        if (childCount > 0) {
            throw new SysException("存在子部门,请先删除子部门");
        }

        // 已绑定用户时拒绝删除
        if (userDeptService.existsByDeptId(deptId)) {
            throw new SysException("该部门已绑定用户,请先解除关联");
        }

        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;
        this.baseMapper.deleteById(deptId, deleteUid);

        log.info("部门已删除: deptId={}, tenantId={}", deptId, dept.getTenantId());
    }

    @Override
    public Dept getDeptById(Long deptId) {
        if (deptId == null) {
            return null;
        }
        return this.getById(deptId);
    }

    // ======================== 私有方法 ========================

    private void fillDefault(Dept dept) {
        dept.setDeptId(null);
        if (dept.getParentId() == null) {
            dept.setParentId(TOP_PARENT_ID);
        }
        if (dept.getSort() == null) {
            dept.setSort(0);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }
    }

    /**
     * 校验上级部门存在（顶级部门除外）
     *
     * @param parentId 上级部门 ID
     * @param deptId   当前部门 ID（新增为 null）
     */
    private void validateParent(Long parentId, Long deptId) {
        long parent = parentId == null ? TOP_PARENT_ID : parentId;
        if (parent == TOP_PARENT_ID) {
            return;
        }
        Dept parentDept = this.getById(parent);
        if (parentDept == null) {
            throw new SysException("上级部门不存在");
        }
        if (deptId != null && deptId.equals(parent)) {
            throw new SysException("上级部门不能是自身");
        }
    }

    /**
     * 校验同级部门名是否已存在
     */
    private boolean checkDeptName(String tenantId, Long parentId, String deptName) {
        return checkDeptName(tenantId, parentId, deptName, null);
    }

    /**
     * 校验同级部门名是否已存在（排除自身，用于更新）
     */
    private boolean checkDeptName(String tenantId, Long parentId, String deptName, Long deptId) {
        LambdaQueryWrapper<Dept> qw = new LambdaQueryWrapper<>();
        qw.eq(Dept::getTenantId, tenantId)
                .eq(Dept::getParentId, parentId)
                .eq(Dept::getDeptName, deptName);
        if (deptId != null) {
            qw.ne(Dept::getDeptId, deptId);
        }
        return this.count(qw) > 0;
    }

    private Dept getDeptOrThrow(Long deptId) {
        Dept dept = this.getById(deptId);
        if (dept == null) {
            throw new SysException(ResponseCode.CHECK_NO_DATA);
        }
        return dept;
    }

    private void buildChildren(DeptVO parent, Map<Long, List<DeptVO>> group) {
        List<DeptVO> children = group.get(parent.getDeptId());
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        parent.setChildren(children);
        children.forEach(child -> buildChildren(child, group));
    }
}

