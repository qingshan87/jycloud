package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.user.dto.DictQueryDTO;
import com.jycloud.api.user.entity.Dict;
import com.jycloud.api.user.vo.DictVO;

import java.util.List;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface DictService extends IService<Dict> {

    /**
     * 新增字典（parentId 为空或 0 表示字典类型，否则为字典项）
     *
     * @param dict 字典实体
     * @return 保存后的字典
     */
    Dict create(Dict dict);

    /**
     * 修改字典（类型节点可改 code 并同步子项；字典项不允许改 code/parentId）
     *
     * @param dict 字典实体
     * @return 更新后的字典
     */
    Dict update(Dict dict);

    /**
     * 条件分页查询字典列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<DictVO> pageByCondition(DictQueryDTO queryDTO);

    /**
     * 根据主键查询字典
     *
     * @param dictId 主键
     * @return 字典
     */
    Dict getDictById(Long dictId);

    /**
     * 查询字典树（顶级类型 + 子字典项）
     *
     * @return 字典树
     */
    List<DictVO> listDictTree();

    /**
     * 根据字典码查询启用状态的字典项（供下拉框/枚举使用）
     *
     * @param code 字典码
     * @return 字典项列表
     */
    List<DictVO> listItemsByCode(String code);

    /**
     * 根据字典码查询字典（返回类型节点及下属子字典项）
     *
     * @param code 字典码
     * @return 字典（含子节点），不存在时返回 null
     */
    DictVO getDictByCode(String code);

    /**
     * 删除字典（级联删除其下所有字典项）
     *
     * @param dictId 主键
     */
    void deleteDict(Long dictId);

}
