package com.hmetao.code_dictionary.utils;

import org.springframework.beans.BeanUtils;

public class MapUtils {
    public static <T> T beanMap(Object source, Class<T> mapClazz) {
        T target = null;
        try {
            target = mapClazz.newInstance();
            BeanUtils.copyProperties(source, target);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return target;
    }
}
