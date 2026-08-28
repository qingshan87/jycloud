package com.jycloud.tool.mp.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycloud.tool.common.base.QueryRequest;
import com.jycloud.tool.common.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理排序工具类
 */
@SuppressWarnings("unchecked")
public class SortUtil {
    // 排序规则： descend 降序
    public static final String ORDER_DESC = "descend";
    // 排序规则： ascend 升序
    public static final String ORDER_ASC = "ascend";

    // ======================== 增强：多字段 + XML 支持 ========================

    /**
     * 解析排序参数为 OrderItem 列表（支持逗号分隔多字段）
     * <pre>
     *   sortField = "username,createTime"
     *   sortOrder = "ascend,descend"
     *   → [OrderItem.asc("username"), OrderItem.desc("create_time")]
     * </pre>
     *
     * @param request           QueryRequest
     * @param camelToUnderscore 是否驼峰转下划线
     * @return 排序项列表，若无有效排序则返回默认值
     */
    public static List<OrderItem> parseOrderItems(QueryRequest request, boolean camelToUnderscore) {
        return parseOrderItems(request, null, null, camelToUnderscore);
    }

    /**
     * 解析排序参数为 OrderItem 列表（带默认排序）
     */
    public static List<OrderItem> parseOrderItems(QueryRequest request, String defaultSort, String defaultOrder, boolean camelToUnderscore) {
        List<OrderItem> result = new ArrayList<>();
        String sortFieldParam = request.getSortField();
        String sortOrderParam = request.getSortOrder();

        if (StringUtils.isNotBlank(sortFieldParam)) {
            String[] fields = sortFieldParam.split(",");
            String[] orders = (sortOrderParam != null) ? sortOrderParam.split(",") : new String[0];

            for (int i = 0; i < fields.length; i++) {
                String field = fields[i].trim();
                if (StringUtils.isBlank(field) || "undefined".equalsIgnoreCase(field)) {
                    continue;
                }
                String order = resolveOrder(orders, i, defaultOrder);
                if (order == null) {
                    continue;
                }
                if (camelToUnderscore) {
                    field = CommonUtil.camelToUnderscore(field);
                }
                result.add(ORDER_DESC.equals(order) ? OrderItem.desc(field) : OrderItem.asc(field));
            }
        }

        // 无有效排序时走默认
        if (result.isEmpty() && StringUtils.isNotBlank(defaultSort)) {
            String field = camelToUnderscore ? CommonUtil.camelToUnderscore(defaultSort) : defaultSort;
            result.add(ORDER_DESC.equalsIgnoreCase(defaultOrder) ? OrderItem.desc(field) : OrderItem.asc(field));
        }
        return result;
    }

    /**
     * 生成 ORDER BY 子句字符串（用于 XML Mapper 的 ${} 占位）
     * <pre>
     *   tableAlias = "u"
     *   → "u.user_id DESC, u.create_time ASC"
     * </pre>
     *
     * @param request           QueryRequest
     * @param defaultSort       默认排序字段
     * @param defaultOrder      默认排序方向
     * @param tableAlias        表别名（如 "u"），传 null 则不拼前缀
     * @param camelToUnderscore 是否驼峰转下划线
     * @return ORDER BY 子句（含 "ORDER BY" 关键字），无排序规则时返回空串
     */
    public static String toOrderClause(QueryRequest request, String defaultSort, String defaultOrder, String tableAlias, boolean camelToUnderscore) {
        List<OrderItem> items = parseOrderItems(request, defaultSort, defaultOrder, camelToUnderscore);
        if (items.isEmpty()) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder("ORDER BY ");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            if (StringUtils.isNotBlank(tableAlias)) {
                sb.append(tableAlias).append(".");
            }
            sb.append(items.get(i).getColumn());
            sb.append(items.get(i).isAsc() ? " ASC" : " DESC");
        }
        return sb.toString();
    }

    /**
     * 生成 ORDER BY 子句（无表别名）
     */
    public static String toOrderClause(QueryRequest request, String defaultSort, String defaultOrder, boolean camelToUnderscore) {
        return toOrderClause(request, defaultSort, defaultOrder, null, camelToUnderscore);
    }

    /**
     * 多字段排序 —— 直接应用到 Page
     */
    public static void handlePageSortMulti(QueryRequest request, Page<?> page, String defaultSort, String defaultOrder, boolean camelToUnderscore) {
        page.setCurrent(request.getPageNum());
        page.setSize(request.getPageSize());
        List<OrderItem> orderItems = parseOrderItems(request, defaultSort, defaultOrder, camelToUnderscore);
        page.addOrder(orderItems);
    }

    /**
     * 多字段排序 —— 直接应用到 QueryWrapper
     */
    public static void handleWrapperSortMulti(QueryRequest request, QueryWrapper<?> wrapper, String defaultSort, String defaultOrder, boolean camelToUnderscore) {
        List<OrderItem> orderItems = parseOrderItems(request, defaultSort, defaultOrder, camelToUnderscore);
        for (OrderItem item : orderItems) {
            if (item.isAsc()) {
                wrapper.orderByAsc(item.getColumn());
            } else {
                wrapper.orderByDesc(item.getColumn());
            }
        }
    }

    // ======================== 内部工具 ========================

    /**
     * 从排序数组中解析第 index 位的方向，若不足则取 defaultOrder
     */
    private static String resolveOrder(String[] orders, int index, String defaultOrder) {
        String order;
        if (index < orders.length) {
            order = orders[index].trim();
        } else if (orders.length > 0) {
            order = orders[0].trim();
        } else if (StringUtils.isNotBlank(defaultOrder)) {
            order = defaultOrder;
        } else {
            return null;
        }
        if (ORDER_DESC.equalsIgnoreCase(order)) {
            return ORDER_DESC;
        }
        if (ORDER_ASC.equalsIgnoreCase(order)) {
            return ORDER_ASC;
        }
        return null;
    }
}
