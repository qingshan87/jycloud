package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jycloud.api.user.dto.InviteUserQueryDTO;
import com.jycloud.api.user.dto.UserQueryDTO;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.vo.UserVO;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-23
 */
public interface UserService extends IService<User> {


    /**
     * 分页查询租户的用户列表
     */
    IPage<UserVO> pageByTenantId(UserQueryDTO queryDTO, String tenantId);

    /**
     * 分页查询用户列表
     * 查询整个系统的用户列表
     *
     */
    IPage<UserVO> pageByCondition(UserQueryDTO queryDTO);

    /**
     * 新增用户
     */
    User create(User user);

    /**
     * 新增用户并绑定到指定租户（setting 平台使用）
     *
     * @param user     用户信息
     * @param tenantId 租户ID
     * @return 创建后的用户
     */
    User createForTenant(User user, String tenantId);

    /**
     * 修改用户
     * 禁止更新 用户名和密码
     *
     */
    User update(User user);

    /**
     * 编辑个人信息（当前登录用户）
     * 仅允许修改：用户头像、真实姓名、邮箱、联系电话、性别、描述
     *
     * @param user 个人信息（userId 会被覆盖为当前登录用户ID）
     * @return 更新后的用户
     */
    User updateProfile(User user);

    /**
     * 更新用户名
     */
    void updateUsername(Long userId, String username);

    /**
     * 更新密码
     */
    void updatePassword(String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(List<Long> userIds);

    /**
     * 删除用户（逻辑删除）
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 更新用户最后登录 IP
     *
     * @param userId 用户ID
     * @param ip     客户端 IP
     */
    void updateLastLoginIp(Long userId, String ip);

    /**
     * 通过用户id获取用户
     *
     * @param userId
     * @return
     */
    User getUserById(Long userId);

    /**
     * 通过手机号码获取用户
     *
     * @param mobile
     * @return
     */
    User getUserByMobile(String mobile);

    /**
     * 通过手机号码获取用户
     *
     * @param mobiles
     * @return
     */
    List<User> getUsersByMobiles(List<String> mobiles);

    /**
     * 通过用户名查找用户
     *
     * @param username username
     * @return user
     */
    User getUserByUsername(String username);

    /**
     * 通过身份证查找用户
     *
     * @param idCardNo idCardNo
     * @return user
     */
    User getUserByIdCardNo(String idCardNo);

    /**
     * 通过unionid查找用户
     *
     * @param unionid
     * @return
     */
    User getUserByUnionid(String unionid);


    /**
     * 按租户和关键字查询用户列表
     * 关键字匹配手机号码或真实姓名
     *
     * @param keyword  关键字
     * @param tenantId 租户ID
     * @return 用户列表
     */
    List<UserVO> listByTenantIdAndKeyword(String keyword, String tenantId);

    /**
     * 查询邀请用户列表
     * <p>
     * 通过账号/真实姓名/手机号码查询系统用户，不按租户过滤；
     * 未携带任何查询条件时返回空列表。
     * </p>
     *
     * @param queryDTO 查询条件（username/realname/mobile）
     * @return 用户列表
     */
    List<UserVO> listInviteUsers(InviteUserQueryDTO queryDTO);
}
