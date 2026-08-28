package com.jycloud.service.user.service.impl;

import com.jycloud.api.user.entity.UserDataPermission;
import com.jycloud.service.user.repository.mapper.UserDataPermissionMapper;
import com.jycloud.service.user.service.UserDataPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户数据权限表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
public class UserDataPermissionServiceImpl extends ServiceImpl<UserDataPermissionMapper, UserDataPermission> implements UserDataPermissionService {

}
