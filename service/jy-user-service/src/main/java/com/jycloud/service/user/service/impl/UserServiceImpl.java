package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jycloud.api.user.constant.UcConstant;
import com.jycloud.api.user.dto.InviteUserQueryDTO;
import com.jycloud.api.user.dto.UserQueryDTO;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.entity.UserTenant;
import com.jycloud.api.user.vo.UserVO;
import com.jycloud.service.user.assemble.UserDetailAssembleService;
import com.jycloud.service.user.cache.TokenCache;
import com.jycloud.service.user.cache.UserCache;
import com.jycloud.service.user.repository.mapper.UserMapper;
import com.jycloud.service.user.service.UserService;
import com.jycloud.service.user.service.UserTenantService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.CommonUtil;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import com.jycloud.tool.common.util.MD5Util;
import com.jycloud.tool.common.util.ValidatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jycloud.api.user.constant.UcConstant.SUPER_ADMIN_ID;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserCache userCache;
    private final TokenCache tokenCache;
    private final UserTenantService userTenantService;
    private final UserDetailAssembleService userDetailAssembleService;

    @Override
    public IPage<UserVO> pageByTenantId(UserQueryDTO queryDTO, String tenantId) {
        Page<UserVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "user_id", SortUtil.ORDER_DESC, false);
        IPage<UserVO> result = this.baseMapper.selectPageByTenantId(page, queryDTO, tenantId);
        
        // 填充用户角色、部门、数据权限等关联信息
        userDetailAssembleService.fillAssociations(result.getRecords(), tenantId);
        return result;
    }

    @Override
    public IPage<UserVO> pageByCondition(UserQueryDTO queryDTO) {
        User user = DtoEntityUtil.trans(queryDTO, User.class);
        QueryWrapper queryWrapper = new QueryWrapper(user);

        Page<User> page = new Page<>();
        SortUtil.handlePageSortMulti(queryDTO, page, "user_id", SortUtil.ORDER_DESC, false);
        IPage<User> result = this.baseMapper.selectPage(page, queryWrapper);

        IPage<UserVO> convert = result.convert(it -> DtoEntityUtil.trans(it, UserVO.class));

        return convert;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User create(User user) {
        // 默认绑定当前登录租户，兼容 ucenter 等按上下文租户建用户的场景
        return createForTenant(user, UserContext.getTenantId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createForTenant(User user, String tenantId) {
        if (user == null || StringUtils.isBlank(tenantId)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        // 账号格式校验
        if (StringUtils.isBlank(user.getUsername()) || !ValidatorUtil.isValidAccount(user.getUsername())) {
            throw new SysException("账号必须以字母开头，后面可以是字母、数字和下划线，总长度在6到20个字符之间");
        }
        validateUniquenessForCreate(user);
        // 生成独立随机盐，终身不变
        String salt = MD5Util.generateSalt();
        user.setSalt(salt);
        user.setPassword(MD5Util.encryptWithSalt(UcConstant.USER_PASSWORD, salt));
        user.setStatus(1);
        this.baseMapper.insert(user);

        // 添加租户关联
        UserTenant userTenant = new UserTenant();
        userTenant.setTenantId(tenantId);
        userTenant.setUserId(user.getUserId());
        userTenant.setIfDefault(1);
        userTenant.setStatus(1);
        userTenantService.setTenant(userTenant);

        this.userCache.cacheUser(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User update(User user) {
        if (SUPER_ADMIN_ID.equals(String.valueOf(user.getUserId())) && !SUPER_ADMIN_ID.equals(UserContext.getUserId())){
            throw new SysException("无权限修改超级管理员账号");
        }

        validateUniquenessForUpdate(user);
        // 查询旧数据，用于清除旧缓存（如旧手机号/用户名的索引）
        User oldUser = this.getUserById(user.getUserId());
        if (oldUser == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        user.setUsername(null);
        user.setPassword(null);
        user.setSalt(null);
        this.baseMapper.updateById(user);
        // 清除旧缓存
        this.userCache.delUser(oldUser);
        // 重新缓存新数据
        User newUser = this.baseMapper.selectById(user.getUserId());
        if (newUser != null) {
            this.userCache.cacheUser(newUser);
            // 如果用户禁用，则清除缓存token
            if (newUser.getStatus() == 0) {
                this.tokenCache.deleteToken(newUser.getUserId().toString());
            }
        }
        return newUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateProfile(User user) {
        String currentUserId = UserContext.getUserId();
        if (StringUtils.isBlank(currentUserId)) {
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        // 使用当前登录用户ID，复用 update 完成更新（唯一性校验、缓存刷新均在其中处理）
        user.setUserId(Long.valueOf(currentUserId));
        return update(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUsername(Long userId, String username) {
        if (userId == null || StringUtils.isBlank(username)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        // 账号格式校验
        if (!ValidatorUtil.isValidAccount(username)) {
            throw new SysException("账号必须以字母开头，后面可以是字母、数字和下划线，总长度在6到20个字符之间");
        }
        if (SUPER_ADMIN_ID.equals(String.valueOf(userId))) {
            throw new SysException("不能修改超级管理员账号");
        }

        User oldUser = this.getUserById(userId);
        if (oldUser == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        // 用户名唯一性校验（排除自身）
        if (checkUsername(username, String.valueOf(userId))) {
            throw new SysException("用户名已存在");
        }

        // 只更新用户名，salt 与密码保持不变
        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setUsername(username);
        this.baseMapper.updateById(updateUser);

        // 清除旧缓存，重建新缓存
        this.userCache.delUser(oldUser);
        User newUser = this.baseMapper.selectById(userId);
        if (newUser != null) {
            this.userCache.cacheUser(newUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String oldPassword, String newPassword) {
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        String currentUserId = UserContext.getUserId();
        if (StringUtils.isBlank(currentUserId)) {
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        User user = this.getUserById(Long.valueOf(currentUserId));
        if (user == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        // 校验原密码（salt 为该用户随机盐）
        String encryptedOld = MD5Util.encryptWithSalt(oldPassword, user.getSalt());
        if (!encryptedOld.equals(user.getPassword())) {
            throw new SysException("原密码不正确");
        }

        User updateUser = new User();
        updateUser.setUserId(user.getUserId());
        updateUser.setPassword(MD5Util.encryptWithSalt(newPassword, user.getSalt()));
        this.baseMapper.updateById(updateUser);

        // 清除旧缓存，重建新缓存
        this.userCache.delUser(user);
        User newUser = this.baseMapper.selectById(user.getUserId());
        if (newUser != null) {
            this.userCache.cacheUser(newUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        // 超级管理员保护：非超级管理员不能重置超级管理员密码
        boolean containsSuperAdmin = userIds.stream()
                .anyMatch(id -> SUPER_ADMIN_ID.equals(String.valueOf(id)));
        if (containsSuperAdmin && !SUPER_ADMIN_ID.equals(UserContext.getUserId())) {
            throw new SysException("无权限重置超级管理员密码");
        }

        List<User> users = this.baseMapper.selectBatchIds(userIds);
        if (users == null || users.isEmpty()) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        for (User user : users) {
            // 先删旧缓存，再重置为默认密码（salt 保持不变）
            this.userCache.delUser(user);
            User updateUser = new User();
            updateUser.setUserId(user.getUserId());
            updateUser.setPassword(MD5Util.encryptWithSalt(UcConstant.USER_PASSWORD, user.getSalt()));
            this.baseMapper.updateById(updateUser);

            // 重建新缓存
            user.setPassword(updateUser.getPassword());
            this.userCache.cacheUser(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        if (userId == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        // 超级管理员保护：非超级管理员不能删除超级管理员账号
        if (SUPER_ADMIN_ID.equals(String.valueOf(userId)) && !SUPER_ADMIN_ID.equals(UserContext.getUserId())) {
            throw new SysException("无权限删除超级管理员账号");
        }

        User user = this.getUserById(userId);
        if (user == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        // 清除用户缓存与登录 token
        this.userCache.delUser(user);
        this.tokenCache.deleteToken(userId.toString());

        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;
        this.baseMapper.deleteById(userId, deleteUid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginIp(Long userId, String ip) {
        if (userId == null || StringUtils.isBlank(ip)) {
            return;
        }

        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setLastLoginIp(ip);
        this.baseMapper.updateById(updateUser);

        // 同步更新缓存，避免后续登录时读到旧的 IP
        User cachedUser = this.userCache.getUserById(String.valueOf(userId));
        if (cachedUser != null) {
            cachedUser.setLastLoginIp(ip);
            this.userCache.cacheUser(cachedUser);
        }
    }

    @Override
    public User getUserById(Long userId) {
        User user = CommonUtil.selectCacheByTemplate(
                () -> this.userCache.getUserById(String.valueOf(userId)),
                () -> this.getUserByIdSql(userId));
        return user;
    }

    private User getUserByIdSql(Long userId) {
        User user = this.baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        this.userCache.cacheUser(user);
        return user;
    }

    private User getUserByMobileSql(String mobile) {
        User user = this.baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
        if (user != null) {
            this.userCache.cacheUser(user);
        }
        return user;
    }

    private User getUserByUsernameSql(String username) {
        User user = this.baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user != null) {
            this.userCache.cacheUser(user);
        }
        return user;
    }

    private User getUserByIdCardNoSql(String idCardNo) {
        User user = this.baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getIdCardNo, idCardNo));
        if (user != null) {
            this.userCache.cacheUser(user);
        }
        return user;
    }

    @Override
    public User getUserByMobile(String mobile) {
        return CommonUtil.selectCacheByTemplate(
                () -> this.userCache.getUserByMobile(mobile),
                () -> this.getUserByMobileSql(mobile));
    }

    @Override
    public List<User> getUsersByMobiles(List<String> mobiles) {
        if (mobiles == null || mobiles.isEmpty()) {
            return List.of();
        }
        List<User> result = new ArrayList<>();
        List<String> missedMobiles = new ArrayList<>();
        for (String mobile : mobiles) {
            User cached = this.userCache.getUserByMobile(mobile);
            if (cached != null) {
                result.add(cached);
            } else {
                missedMobiles.add(mobile);
            }
        }
        if (!missedMobiles.isEmpty()) {
            List<User> dbUsers = this.baseMapper.selectList(
                    new LambdaQueryWrapper<User>().in(User::getMobile, missedMobiles));
            for (User user : dbUsers) {
                this.userCache.cacheUser(user);
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public User getUserByUsername(String username) {
        return CommonUtil.selectCacheByTemplate(
                () -> this.userCache.getUserByUsername(username),
                () -> this.getUserByUsernameSql(username));
    }

    @Override
    public User getUserByIdCardNo(String idCardNo) {
        return CommonUtil.selectCacheByTemplate(
                () -> this.userCache.getUserByIdCard(idCardNo),
                () -> this.getUserByIdCardNoSql(idCardNo));
    }

    @Override
    public User getUserByUnionid(String unionid) {
        return CommonUtil.selectCacheByTemplate(
                () -> this.userCache.getUserByUnionid(unionid),
                () -> this.getUserByUnionidSql(unionid));
    }

    @Override
    public List<UserVO> listByTenantIdAndKeyword(String keyword, String tenantId) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(tenantId)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        List<UserVO> result = this.baseMapper.selectByTenantIdAndKeyword(keyword, tenantId);
        return result;
    }

    @Override
    public List<UserVO> listInviteUsers(InviteUserQueryDTO queryDTO) {
        // 未携带查询条件时直接返回空，避免全量查询
        if (queryDTO == null
                || (StringUtils.isBlank(queryDTO.getUsername())
                && StringUtils.isBlank(queryDTO.getRealname())
                && StringUtils.isBlank(queryDTO.getMobile()))) {
            return Collections.emptyList();
        }

        // 直接查询系统用户，不按租户过滤
        User entity = DtoEntityUtil.trans(queryDTO, User.class);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>(entity);
        List<User> users = this.list(queryWrapper);

        return users.stream()
                .map(it -> DtoEntityUtil.trans(it, UserVO.class))
                .collect(Collectors.toList());
    }

    private User getUserByUnionidSql(String unionid) {
        User user = this.baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUnionid, unionid));
        if (user != null) {
            this.userCache.cacheUser(user);
        }
        return user;
    }

    public boolean checkMobile(String mobile) {
        return StringUtils.isNotBlank(mobile) && this.getUserByMobile(mobile) != null;
    }

    public boolean checkMobile(String mobile, String userId) {
        if (StringUtils.isBlank(userId)) {
            return this.checkMobile(mobile);
        }

        if (StringUtils.isNotBlank(mobile)) {
            User user = this.getUserByMobile(mobile);
            return user != null && !userId.equals(String.valueOf(user.getUserId()));
        }

        return false;
    }

    private boolean checkUsername(String username) {
        return StringUtils.isNotBlank(username) && this.getUserByUsername(username) != null;
    }

    private boolean checkUsername(String username, String userId) {
        if (StringUtils.isBlank(userId)) {
            return this.checkUsername(username);
        }

        if (StringUtils.isNotBlank(username)) {
            User user = this.getUserByUsername(username);
            return user != null && !userId.equals(String.valueOf(user.getUserId()));
        }

        return false;
    }

    private boolean checkIdCardNo(String idCardNo) {
        return StringUtils.isNotBlank(idCardNo) && this.getUserByIdCardNo(idCardNo) != null;
    }

    private boolean checkIdCardNo(String idCardNo, String userId) {
        if (StringUtils.isBlank(userId)) {
            return this.checkIdCardNo(idCardNo);
        }

        if (StringUtils.isNotBlank(idCardNo)) {
            User user = this.getUserByIdCardNo(idCardNo);
            return user != null && !userId.equals(String.valueOf(user.getUserId()));
        }

        return false;
    }

    private boolean checkUnionid(String unionid) {
        return StringUtils.isNotBlank(unionid) && this.getUserByUnionid(unionid) != null;
    }

    private boolean checkUnionid(String unionid, String userId) {
        if (StringUtils.isBlank(userId)) {
            return this.checkUnionid(unionid);
        }
        if (StringUtils.isNotBlank(unionid)) {
            User user = this.getUserByUnionid(unionid);
            return user != null && !userId.equals(String.valueOf(user.getUserId()));
        }
        return false;
    }

    // ======================== 唯一性校验 ========================

    private void validateUniquenessForCreate(User user) {
        if (StringUtils.isNotBlank(user.getUnionid()) && checkUnionid(user.getUnionid())) {
            throw new SysException("unionid已存在");
        }
        if (StringUtils.isNotBlank(user.getUsername()) && checkUsername(user.getUsername())) {
            throw new SysException("用户名已存在");
        }
        if (StringUtils.isNotBlank(user.getMobile()) && checkMobile(user.getMobile())) {
            throw new SysException(ResponseCode.MOBILE_IS_EXIST);
        }
        if (StringUtils.isNotBlank(user.getIdCardNo()) && checkIdCardNo(user.getIdCardNo())) {
            throw new SysException("身份证号已存在");
        }
    }

    private void validateUniquenessForUpdate(User user) {
        String userId = String.valueOf(user.getUserId());
        if (StringUtils.isNotBlank(user.getUnionid()) && checkUnionid(user.getUnionid(), userId)) {
            throw new SysException("unionid已存在");
        }
        if (StringUtils.isNotBlank(user.getUsername()) && checkUsername(user.getUsername(), userId)) {
            throw new SysException("用户名已存在");
        }
        if (StringUtils.isNotBlank(user.getMobile()) && checkMobile(user.getMobile(), userId)) {
            throw new SysException(ResponseCode.MOBILE_IS_EXIST);
        }
        if (StringUtils.isNotBlank(user.getIdCardNo()) && checkIdCardNo(user.getIdCardNo(), userId)) {
            throw new SysException("身份证号已存在");
        }
    }
}
