package com.jycloud.tool.mp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 多租户配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "sys.tenant")
public class TenantProperties {

    /**
     * 多租户字段名称
     */
    private String column = "tenant_id";

    /**
     * 是否启用了租户筛选功能
     */
    private Boolean enabled = true;

    /**
     * 多租户数据表
     */
    private List<String> tables = new ArrayList<>();

    /**
     * 无需进行租户筛选的表格
     */
    private List<String> ignoreTables = new ArrayList<>();

}
