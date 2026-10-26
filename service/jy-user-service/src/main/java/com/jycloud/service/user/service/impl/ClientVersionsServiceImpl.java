package com.jycloud.service.user.service.impl;

import com.jycloud.api.user.entity.ClientVersions;
import com.jycloud.service.user.repository.mapper.ClientVersionsMapper;
import com.jycloud.service.user.service.ClientVersionsService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 应用版本管理记录 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
public class ClientVersionsServiceImpl extends ServiceImpl<ClientVersionsMapper, ClientVersions> implements ClientVersionsService {

}
