package com.jycloud.tool.mp.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author Lqs
 * @date 2025/10/23 10:13
 */
public class QueryHelper {

    public static <T> void applyRange(
            LambdaQueryWrapper<T> qw,
            SFunction<T, ?> column,
            String rangeStr,
            Class<?> targetType) {

        if (StringUtils.isBlank(rangeStr)) return;

        String[] parts = rangeStr.split(",", -1);
        Object min = parseValue(parts[0], targetType);
        Object max = parts.length > 1 ? parseValue(parts[1], targetType) : null;

        if (min != null) {
            qw.ge(column, min);
        }
        if (max != null) {
            qw.le(column, max);
        }
    }

    private static Object parseValue(String str, Class<?> type) {
        if (StringUtils.isBlank(str)) return null;
        str = str.trim();
        try {
            if (type == BigDecimal.class) {
                return new BigDecimal(str);
            } else if (type == Integer.class) {
                return Integer.valueOf(str);
            } else if (type == Double.class) {
                return Double.valueOf(str);
            }
            throw new UnsupportedOperationException("不支持的类型: " + type);
        } catch (Exception e) {
            throw new IllegalArgumentException("参数格式错误: " + str, e);
        }
    }
}
