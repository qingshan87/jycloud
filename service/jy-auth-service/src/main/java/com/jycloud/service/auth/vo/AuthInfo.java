package com.jycloud.service.auth.vo;

import com.jycloud.api.user.vo.UserTenantVO;
import com.jycloud.api.user.vo.UserVO;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AuthInfo {
    private String token;
    private String exipreTime;
    private Set<String> permissions;
    private Set<String> roles;
    private UserVO user;
    private String tenantId;
    private UserTenantVO tenantDto;
    private List<UserTenantVO> tenantDtos;
}
