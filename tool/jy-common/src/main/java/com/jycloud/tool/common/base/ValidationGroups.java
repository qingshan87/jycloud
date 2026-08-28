package com.jycloud.tool.common.base;


import jakarta.validation.groups.Default;

/**
 * @author Lqs
 * @date 2022/8/5 10:06
 */
public class ValidationGroups {

    /**
     * 新增
     */
    public interface Insert extends Default {
    }

    /**
     * 更新
     */
    public interface Update extends Default{
    }

    /**
     * 查看详情
     */
    public interface Detail extends Default {
    }


    /**
     * 删除
     */
    public interface Delete extends Default{
    }

}
