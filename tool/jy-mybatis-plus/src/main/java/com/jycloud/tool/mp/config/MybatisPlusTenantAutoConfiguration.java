package com.jycloud.tool.mp.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.jycloud.tool.common.constant.TenantConstant;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.util.JWTUtil;
import com.jycloud.tool.mp.properties.TenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration(after = MybatisPlusAutoConfiguration.class)
@ConditionalOnClass({MybatisPlusInterceptor.class, TenantLineHandler.class})
@EnableConfigurationProperties(TenantProperties.class)
@ConditionalOnProperty(prefix = "sys.tenant", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MybatisPlusTenantAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(TenantProperties tenantProperties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantLineHandler(tenantProperties)));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantLineHandler tenantLineHandler(TenantProperties tenantProperties) {
        return new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue(resolveTenantId());
            }

            @Override
            public String getTenantIdColumn() {
                return tenantProperties.getColumn();
            }

            @Override
            public boolean ignoreTable(String tableName) {
                if (containsIgnoreCase(tenantProperties.getIgnoreTables(), tableName)) {
                    return true;
                }
                List<String> tables = tenantProperties.getTables();
                System.out.println(!(CollectionUtils.isNotEmpty(tables) && containsIgnoreCase(tables, tableName)));
                return !(CollectionUtils.isNotEmpty(tables) && containsIgnoreCase(tables, tableName));
            }

            @Override
            public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
                return columns.stream()
                        .map(Column::getColumnName)
                        .anyMatch(columnName -> StringUtils.equalsIgnoreCase(cleanColumnName(columnName), tenantIdColumn));
            }
        };
    }

    private static String resolveTenantId() {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            tenantId = JWTUtil.getTenantId();
        }
        return StringUtils.defaultIfBlank(tenantId, TenantConstant.DEFAULT_TENANT_ID);
    }

    private static boolean containsIgnoreCase(List<String> source, String value) {
        return CollectionUtils.isNotEmpty(source)
                && source.stream().anyMatch(item -> StringUtils.equalsIgnoreCase(item, value));
    }

    private static String cleanColumnName(String columnName) {
        return StringUtils.remove(StringUtils.remove(columnName, "`"), "\"");
    }
}
