package com.jycloud.tool.mp.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jycloud.tool.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Lqs
 * @date 2022/11/18 12:11
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class MpMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime date = LocalDateTime.now();

        Object tenantId = this.getFieldValByName("tenantId", metaObject);
        if (tenantId == null) {
            this.setFieldValByName("tenantId", this.getTenantId(), metaObject);
        }
        Object createUid = this.getFieldValByName("createUid", metaObject);
        if (createUid == null) {
            this.setFieldValByName("createUid", this.getUserId(), metaObject);
        }

        this.setFieldValByName("createTime", date, metaObject);
        this.setFieldValByName("updateTime", date, metaObject);
        this.setFieldValByName("updateUid", 0l, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);

        Object updateUid = this.getFieldValByName("updateUid", metaObject);
        if (updateUid == null || Objects.equals(updateUid, 0l)) {
            // 未设置更新用户ID
            Long userId = this.getUserId();
            if (userId > 0) {
                this.setFieldValByName("updateUid", userId, metaObject);
            }
        }
    }

    private Long getUserId() {
        String userId = UserContext.getUserId();
        return StringUtils.isNotBlank(userId) ? Long.valueOf(userId) : 0l;
    }

    private String getTenantId() {
        String tenantId = UserContext.getTenantId();
        return tenantId;
    }
}
