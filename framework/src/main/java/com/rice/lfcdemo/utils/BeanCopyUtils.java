package com.rice.lfcdemo.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {
    private BeanCopyUtils() {}

    public static <V> V copy(Object source, Class<V> target) {
        V result = null;

        try {
            result = target.newInstance();
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <O, V> List<V> copyList(List<O> list, Class<V> target) {
        return list.stream().map(o -> copy(o, target)).collect(Collectors.toList());
    }
}
