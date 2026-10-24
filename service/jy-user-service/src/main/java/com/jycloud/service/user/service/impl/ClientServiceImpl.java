package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jycloud.api.user.constant.UcConstant;
import com.jycloud.api.user.dto.ClientQueryDTO;
import com.jycloud.api.user.entity.Client;
import com.jycloud.api.user.vo.ClientVO;
import com.jycloud.service.user.repository.mapper.ClientMapper;
import com.jycloud.service.user.service.ClientService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.AppUtil;
import com.jycloud.tool.common.util.MD5Util;
import com.jycloud.tool.mp.utils.SortUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 业务系统表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Client create(Client client) {
        if (client == null || StringUtils.isBlank(client.getAppName())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        // 应用名称唯一性校验
        if (checkAppName(client.getAppName())) {
            throw new SysException("应用名称已存在");
        }

        // 生成随机4位 clientId（校验唯一）
        client.setClientId(generateUniqueClientId());
        // 生成盐、appKey、appSecret
        String salt = MD5Util.generateSalt();
        client.setSalt(salt);
        String appKey = AppUtil.generateAppKey();
        client.setAppKey(appKey);
        client.setAppSecret(AppUtil.generateAppSecret(appKey, salt));

        if (client.getStatus() == null) {
            client.setStatus(1);
        }

        this.baseMapper.insert(client);
        return client;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Client update(Client client) {
        if (client == null || client.getId() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        getClientOrThrow(client.getId());
        // 应用名称唯一性校验（排除自身）
        if (checkAppName(client.getAppName(), client.getId())) {
            throw new SysException("应用名称已存在");
        }

        // 敏感/系统生成字段不允许修改
        client.setClientId(null);
        client.setAppKey(null);
        client.setAppSecret(null);
        client.setSalt(null);

        this.baseMapper.updateById(client);
        return this.baseMapper.selectById(client.getId());
    }

    @Override
    public IPage<ClientVO> pageByCondition(ClientQueryDTO queryDTO) {
        Page<ClientVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "id", SortUtil.ORDER_DESC, true);
        return this.baseMapper.selectClientPage(page, queryDTO);
    }

    @Override
    public Client getClientById(Long id) {
        if (id == null) {
            return null;
        }
        return this.getById(id);
    }

    @Override
    public Client getClientByClientId(String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<Client>().eq(Client::getClientId, clientId));
    }

    @Override
    public List<Client> listByClientIds(Collection<String> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyList();
        }
        return this.list(new LambdaQueryWrapper<Client>()
                .in(Client::getClientId, clientIds)
                .eq(Client::getStatus, 1)
                .orderByAsc(Client::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Client resetSecret(Long id) {
        if (id == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        getClientOrThrow(id);

        // 重新生成盐、appKey、appSecret
        String salt = MD5Util.generateSalt();
        String appKey = AppUtil.generateAppKey();
        String appSecret = AppUtil.generateAppSecret(appKey, salt);

        Client updateClient = new Client();
        updateClient.setId(id);
        updateClient.setSalt(salt);
        updateClient.setAppKey(appKey);
        updateClient.setAppSecret(appSecret);
        this.baseMapper.updateById(updateClient);

        return this.baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClient(Long id) {
        if (id == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        Client client = getClientOrThrow(id);
        if (isBuiltInClient(client)) {
            throw new SysException("设置平台和用户中心不允许删除");
        }

        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;
        this.baseMapper.deleteById(id, deleteUid);
    }

    // ======================== 私有方法 ========================

    /**
     * 生成随机4位 clientId，校验数据库唯一后返回
     */
    private String generateUniqueClientId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 100; i++) {
            int candidate = random.nextInt(1000, 10000);
            String clientId = String.valueOf(candidate);
            if (getClientByClientId(clientId) == null) {
                return clientId;
            }
        }
        throw new SysException("生成应用ID失败,请重试");
    }

    /**
     * 检查应用名称是否已存在
     */
    private boolean checkAppName(String appName) {
        return checkAppName(appName, null);
    }

    /**
     * 检查应用名称是否已存在（排除指定ID，用于更新）
     */
    private boolean checkAppName(String appName, Long id) {
        if (StringUtils.isBlank(appName)) {
            return false;
        }
        LambdaQueryWrapper<Client> qw = new LambdaQueryWrapper<>();
        qw.eq(Client::getAppName, appName);
        if (id != null) {
            qw.ne(Client::getId, id);
        }
        return this.count(qw) > 0;
    }

    /**
     * 根据 ID 查询业务系统，不存在则抛出异常
     */
    private Client getClientOrThrow(Long id) {
        Client client = this.getById(id);
        if (client == null) {
            throw new SysException(ResponseCode.CHECK_NO_DATA);
        }
        return client;
    }

    /**
     * 是否为内置平台（设置平台 1111、用户中心 1112）
     */
    private boolean isBuiltInClient(Client client) {
        return client != null && (UcConstant.SETTING_CLIENT_ID.equals(client.getClientId())
                || UcConstant.UCENTER_CLIENT_ID.equals(client.getClientId()));
    }
}
