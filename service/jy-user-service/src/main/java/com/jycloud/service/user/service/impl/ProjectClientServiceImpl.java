package com.jycloud.service.user.service.impl;

import com.jycloud.api.user.entity.ProjectClient;
import com.jycloud.service.user.repository.mapper.ProjectClientMapper;
import com.jycloud.service.user.service.ProjectClientService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 项目应用配置 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
public class ProjectClientServiceImpl extends ServiceImpl<ProjectClientMapper, ProjectClient> implements ProjectClientService {

}
