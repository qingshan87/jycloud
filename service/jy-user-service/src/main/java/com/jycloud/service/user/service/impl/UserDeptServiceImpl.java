package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.user.entity.UserDept;
import com.jycloud.service.user.repository.mapper.UserDeptMapper;
import com.jycloud.service.user.service.UserDeptService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户部门表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Service
public class UserDeptServiceImpl extends ServiceImpl<UserDeptMapper, UserDept> implements UserDeptService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserDept(Long userId, Long deptId) {
        if (userId == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new SysException("租户ID不能为空");
        }

        // 先删后插，保证单部门
        this.remove(new LambdaQueryWrapper<UserDept>()
                .eq(UserDept::getUserId, userId)
                .eq(UserDept::getTenantId, tenantId));

        if (deptId == null) {
            log.info("用户部门已解绑: userId={}, tenantId={}", userId, tenantId);
            return;
        }

        UserDept userDept = new UserDept();
        userDept.setUserId(userId);
        userDept.setDeptId(deptId);
        userDept.setTenantId(tenantId);
        this.save(userDept);
        log.info("用户部门已设置: userId={}, deptId={}, tenantId={}", userId, deptId, tenantId);
    }

    @Override
    public UserDept getUserDept(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<UserDept>()
                .eq(UserDept::getUserId, userId)
                .eq(UserDept::getTenantId, tenantId));
    }

    @Override
    public boolean existsByDeptId(Long deptId) {
        if (deptId == null) {
            return false;
        }
        return this.count(new LambdaQueryWrapper<UserDept>().eq(UserDept::getDeptId, deptId)) > 0;
    }
}
