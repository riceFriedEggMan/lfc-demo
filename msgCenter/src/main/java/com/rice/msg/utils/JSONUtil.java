package com.rice.msg.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONUtil {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        String result = null;
        try {
            result = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json) || null == clazz) {
            return null;
        }
        T result = null;
        try {
            result = mapper.convertValue(json, clazz);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> List<T> parseList(String listJsonStr, Class<T> clazz) {
        if (StringUtils.isBlank(listJsonStr) || clazz == null) return Collections.emptyList();
        List<T> list = Collections.emptyList();
        try {
            list = mapper.readValue(listJsonStr, List.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <K, V> Map<K, V> parseMap(String mapJsonStr, Class<K> kClass, Class<V> vClass){
        if (StringUtils.isBlank(mapJsonStr) || kClass == null || vClass == null) return Collections.emptyMap();
        Map<K, V> map = new HashMap<>();
        try {
            map = mapper.readValue(mapJsonStr, mapper.getTypeFactory().constructParametricType(Map.class, kClass, vClass));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }

}
