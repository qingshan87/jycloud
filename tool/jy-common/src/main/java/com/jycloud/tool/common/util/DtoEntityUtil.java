package com.jycloud.tool.common.util;

import com.alibaba.fastjson2.JSON;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体相互映射
 *
 * @author Lqs
 * @date 2021/9/10 16:11
 */
public class DtoEntityUtil {

    /**
     * 实体类集合转化
     *
     * @param objs
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> trans(List<?> objs, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(objs)) {
            return list;
        }
        for (Object source : objs) {
            if (source != null) {
                T target = JSON.parseObject(JSON.toJSONString(source), clazz);
                //把源对象属性赋值给目标对象
                //BeanUtils.copyProperties(source, target);
                list.add(target);
            }
        }
        return list;
    }

    /**
     * 实体类转化
     *
     * @param obj
     * @param clazz
     * @return
     */
    public static <T> T trans(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        //把源对象类型强制转换为目标对象
        T target = JSON.parseObject(JSON.toJSONString(obj), clazz);
        //把源对象属性赋值给目标对象
        //BeanUtils.copyProperties(obj, target);
        return target;
    }

    public static void main(String[] args) {

        System.out.println(System.getProperty("user.dir"));
    }
}
