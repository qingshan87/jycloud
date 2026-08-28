package com.jycloud.service.user.service.impl;

import com.jycloud.api.user.entity.Dept;
import com.jycloud.service.user.repository.mapper.DeptMapper;
import com.jycloud.service.user.service.DeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

}
