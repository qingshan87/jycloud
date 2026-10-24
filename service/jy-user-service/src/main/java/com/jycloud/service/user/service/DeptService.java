package com.jycloud.service.user.service;

import com.jycloud.api.user.entity.Dept;
import com.jycloud.api.user.vo.DeptVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface DeptService extends IService<Dept> {

    /**
     * 查询租户部门树
     *
     * @param tenantId 租户 ID
     * @return 部门树
     */
    List<DeptVO> listDeptTree(String tenantId);

    /**
     * 新增部门（租户内同级部门名唯一）
     *
     * @param dept 部门实体
     * @return 保存后的部门
     */
    Dept createDept(Dept dept);

    /**
     * 修改部门（不允许设置自身为上级部门）
     *
     * @param dept 部门实体
     * @return 更新后的部门
     */
    Dept updateDept(Dept dept);

    /**
     * 删除部门（存在子部门或已绑定用户时拒绝删除）
     *
     * @param deptId 部门 ID
     */
    void deleteDept(Long deptId);

    /**
     * 根据 ID 查询部门
     *
     * @param deptId 部门 ID
     * @return 部门
     */
    Dept getDeptById(Long deptId);
}
