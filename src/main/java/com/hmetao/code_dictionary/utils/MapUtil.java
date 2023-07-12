package com.hmetao.code_dictionary.utils;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MapUtil {
    public static <T> T beanMap(Object source, Class<T> mapClazz) {
        T target = null;
        try {
            target = mapClazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return target;
    }

    public static <V, T> PageInfo<T> PageInfoCopy(List<V> source, List<T> target) {
        PageInfo<V> pageSource = new PageInfo<>(source);
        PageInfo<T> pageTarget = new PageInfo<>();
        BeanUtils.copyProperties(pageSource, pageTarget);
        pageTarget.setList(target);
        return pageTarget;
    }
}
