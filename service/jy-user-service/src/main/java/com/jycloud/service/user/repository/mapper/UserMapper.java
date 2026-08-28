package com.jycloud.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycloud.api.user.dto.UserQueryDTO;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-23
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 条件查询列表
     */
    List<User> selectByCondition(User user);

    /**
     * 单个逻辑删除
     */
    int deleteById(@Param("userId") Long userId,
                   @Param("deleteUid") Long deleteUid);

    /**
     * 批量逻辑删除
     */
    int deleteByIds(@Param("userIds") List<Long> userIds,
                    @Param("deleteUid") Long deleteUid);

    /**
     * 按租户分页查询用户（INNER JOIN t_user_tenant）
     */
    IPage<UserVO> selectPageByTenantId(Page<UserVO> page,
                                      @Param("queryDTO") UserQueryDTO queryDTO,
                                      @Param("tenantId") String tenantId);

    /**
     * 按租户和关键字查询用户列表
     * @param keyword 关键字, 手机号码或者真实姓名
     * @param tenantId 租户ID
     * @return 用户列表
     */
    List<UserVO> selectByTenantIdAndKeyword(@Param("keyword") String keyword, @Param("tenantId") String tenantId);

}
