package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.user.constant.UcConstant;
import com.jycloud.api.user.dto.TenantClientDTO;
import com.jycloud.api.user.entity.TenantClient;
import com.jycloud.api.user.vo.TenantClientVO;
import com.jycloud.service.user.repository.mapper.TenantClientMapper;
import com.jycloud.service.user.service.ClientService;
import com.jycloud.service.user.service.TenantClientService;
import com.jycloud.service.user.service.TenantService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.constant.TenantConstant;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 租户授权的应用表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantClientServiceImpl extends ServiceImpl<TenantClientMapper, TenantClient> implements TenantClientService {

    private final TenantService tenantService;
    private final ClientService clientService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindClient(TenantClientDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getTenantId()) || StringUtils.isBlank(dto.getClientId())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        // 校验租户与应用是否存在
        if (tenantService.getTenantByTenantId(dto.getTenantId()) == null) {
            throw new SysException("租户不存在");
        }
        if (clientService.getClientByClientId(dto.getClientId()) == null) {
            throw new SysException("应用不存在");
        }

        // 设置平台仅系统租户（默认租户）可绑定，其他租户不允许
        if (UcConstant.SETTING_CLIENT_ID.equals(dto.getClientId())
                && !TenantConstant.DEFAULT_TENANT_ID.equals(dto.getTenantId())) {
            throw new SysException("设置平台仅系统租户可绑定");
        }

        // 是否已存在有效绑定
        TenantClient existing = this.getOne(new LambdaQueryWrapper<TenantClient>()
                .eq(TenantClient::getTenantId, dto.getTenantId())
                .eq(TenantClient::getClientId, dto.getClientId()));
        if (existing != null) {
            throw new SysException("该应用已绑定,请勿重复绑定");
        }

        int status = dto.getStatus() != null ? dto.getStatus() : 1;

        // 存在已移除的绑定记录时，重新启用，避免重复插入
        TenantClient deleted = this.baseMapper.selectIncludeDeleted(dto.getTenantId(), dto.getClientId());
        if (deleted != null) {
            this.baseMapper.revive(deleted.getId(), status);
            return;
        }

        TenantClient tenantClient = new TenantClient();
        tenantClient.setTenantId(dto.getTenantId());
        tenantClient.setClientId(dto.getClientId());
        tenantClient.setStatus(status);
        this.save(tenantClient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindClient(Long id) {
        if (id == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        TenantClient tenantClient = this.getById(id);
        if (tenantClient == null) {
            throw new SysException(ResponseCode.CHECK_NO_DATA);
        }

        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;
        this.baseMapper.deleteById(id, deleteUid);
    }

    @Override
    public List<TenantClientVO> listByTenant(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return List.of();
        }
        return this.baseMapper.selectTenantClientVOList(tenantId);
    }
}
