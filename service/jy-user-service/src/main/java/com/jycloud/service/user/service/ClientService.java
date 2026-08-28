package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jycloud.api.user.dto.ClientQueryDTO;
import com.jycloud.api.user.entity.Client;
import com.jycloud.api.user.vo.ClientVO;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务系统表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface ClientService extends IService<Client> {

    /**
     * 新增业务系统（自动生成 clientId、appKey、appSecret、salt）
     *
     * @param client 业务系统实体
     * @return 保存后的业务系统
     */
    Client create(Client client);

    /**
     * 修改业务系统（clientId、appKey、appSecret、salt 不允许修改）
     *
     * @param client 业务系统实体
     * @return 更新后的业务系统
     */
    Client update(Client client);

    /**
     * 分页查询业务系统列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<ClientVO> pageByCondition(ClientQueryDTO queryDTO);

    /**
     * 根据主键查询业务系统
     *
     * @param id 主键
     * @return 业务系统
     */
    Client getClientById(Long id);

    /**
     * 根据 clientId 查询业务系统
     *
     * @param clientId 应用ID
     * @return 业务系统
     */
    Client getClientByClientId(String clientId);

    /**
     * 根据 clientId 集合批量查询启用的业务系统
     *
     * @param clientIds 应用ID集合
     * @return 业务系统列表
     */
    List<Client> listByClientIds(Collection<String> clientIds);

    /**
     * 重置应用秘钥（重新生成 appKey、salt、appSecret）
     *
     * @param id 主键
     * @return 更新后的业务系统（含新秘钥）
     */
    Client resetSecret(Long id);

    /**
     * 删除业务系统（内置平台禁止删除）
     *
     * @param id 主键
     */
    void deleteClient(Long id);

}
