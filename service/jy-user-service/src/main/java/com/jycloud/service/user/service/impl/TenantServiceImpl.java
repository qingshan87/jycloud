package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.user.constant.AdminRoleNameEnum;
import com.jycloud.api.user.dto.RoleDTO;
import com.jycloud.api.user.dto.TenantQueryDTO;
import com.jycloud.api.user.entity.Tenant;
import com.jycloud.api.user.vo.TenantVO;
import com.jycloud.service.user.cache.TenantCache;
import com.jycloud.service.user.repository.mapper.TenantMapper;
import com.jycloud.service.user.service.RoleService;
import com.jycloud.service.user.service.TenantService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.constant.TenantConstant;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.CommonUtil;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 租户表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    private final RoleService roleService;
    private final TenantCache tenantCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant create(Tenant tenant) {
        if (tenant == null || StringUtils.isBlank(tenant.getTenantName())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        // 租户名称唯一性校验
        if (checkTenantName(tenant.getTenantName())) {
            throw new SysException("租户名称已存在");
        }

        // 生成随机6位 tenantId（校验唯一）
        tenant.setTenantId(generateUniqueTenantId());
        if (tenant.getStatus() == null) {
            tenant.setStatus(1);
        }

        this.baseMapper.insert(tenant);
        // 缓存租户
        tenantCache.cacheTenant(tenant);
        // 同时创建租户管理员角色
        createTenantAdminRole(tenant.getTenantId());
        return tenant;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant update(Tenant tenant) {
        if (tenant == null || tenant.getId() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        getTenantOrThrow(tenant.getId());

        // 租户名称唯一性校验（排除自身）
        if (StringUtils.isNotBlank(tenant.getTenantName())
                && checkTenantName(tenant.getTenantName(), tenant.getId())) {
            throw new SysException("租户名称已存在");
        }

        // 租户ID不允许修改
        tenant.setTenantId(null);
        this.baseMapper.updateById(tenant);
        Tenant newTenant = this.baseMapper.selectById(tenant.getId());
        // 更新租户缓存
        if (newTenant != null) {
            tenantCache.cacheTenant(newTenant);
        }
        return newTenant;
    }

    @Override
    public IPage<TenantVO> pageByCondition(TenantQueryDTO queryDTO) {
        Page<Tenant> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "id", SortUtil.ORDER_DESC, true);

        Tenant entity = DtoEntityUtil.trans(queryDTO, Tenant.class);
        LambdaQueryWrapper<Tenant> qw = new LambdaQueryWrapper<>(entity);
    
        return this.page(page, qw).convert(tenant -> DtoEntityUtil.trans(tenant, TenantVO.class));
    }

    @Override
    public Tenant getTenantById(Long id) {
        if (id == null) {
            return null;
        }
        return this.getById(id);
    }

    @Override
    public Tenant getTenantByTenantId(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return null;
        }
        return CommonUtil.selectCacheByTemplate(
                () -> tenantCache.getTenantByTenantId(tenantId),
                () -> getTenantByTenantIdSql(tenantId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenant(Long id) {
        if (id == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        Tenant tenant = getTenantOrThrow(id);
        // 默认租户不允许删除
        if (TenantConstant.DEFAULT_TENANT_ID.equals(tenant.getTenantId())) {
            throw new SysException("默认租户不允许删除");
        }

        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;
        this.baseMapper.deleteById(id, deleteUid);
        // 清除租户缓存
        tenantCache.delTenant(tenant.getTenantId());
    }

    // ======================== 私有方法 ========================

    /**
     * 生成随机6位 tenantId，校验数据库唯一后返回
     */
    private String generateUniqueTenantId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 100; i++) {
            int candidate = random.nextInt(1, 1000000);
            String tenantId = String.format("%06d", candidate);
            if (getTenantByTenantId(tenantId) == null) {
                return tenantId;
            }
        }
        throw new SysException("生成租户ID失败,请重试");
    }

    /**
     * 检查租户名称是否已存在
     */
    private boolean checkTenantName(String tenantName) {
        return checkTenantName(tenantName, null);
    }

    /**
     * 检查租户名称是否已存在（排除指定ID，用于更新）
     */
    private boolean checkTenantName(String tenantName, Long id) {
        if (StringUtils.isBlank(tenantName)) {
            return false;
        }
        LambdaQueryWrapper<Tenant> qw = new LambdaQueryWrapper<>();
        qw.eq(Tenant::getTenantName, tenantName);
        if (id != null) {
            qw.ne(Tenant::getId, id);
        }
        return this.count(qw) > 0;
    }

    /**
     * 根据 ID 查询租户，不存在则抛出异常
     */
    private Tenant getTenantOrThrow(Long id) {
        Tenant tenant = this.getById(id);
        if (tenant == null) {
            throw new SysException(ResponseCode.CHECK_NO_DATA);
        }
        return tenant;
    }

    /**
     * 根据租户ID查询数据库并回写缓存
     */
    private Tenant getTenantByTenantIdSql(String tenantId) {
        Tenant tenant = this.getOne(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantId, tenantId));
        if (tenant != null) {
            tenantCache.cacheTenant(tenant);
        }
        return tenant;
    }

    /**
     * 创建租户管理员角色
     */
    private void createTenantAdminRole(String tenantId) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName(AdminRoleNameEnum.TENANT_ADMIN.getCode());
        roleDTO.setTenantId(tenantId);
        roleDTO.setRemark(AdminRoleNameEnum.TENANT_ADMIN.getDesc());
        roleDTO.setStatus(1);
        roleService.createAdminRole(roleDTO);
    }
}
