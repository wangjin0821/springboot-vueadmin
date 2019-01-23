package com.wiggin.mangersys.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BeanUtil {

    /** Map<source class,<target class , BeanCopier>> */
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Class<?>, BeanCopier>> beanCopierCache = new ConcurrentHashMap<>();

    private static final SerializerFeature[] SERIALIZER_FEATURES = new SerializerFeature[] { SerializerFeature.BrowserSecure, SerializerFeature.DisableCircularReferenceDetect };
    
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
   
    public static final FastJsonConfig FASTJSON_CONFIG = new FastJsonConfig();
    
    static {
        FASTJSON_CONFIG.setDateFormat(DATE_FORMAT);
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        serializeConfig.put(Void.class, ToStringSerializer.instance);
        serializeConfig.put(Void.TYPE, ToStringSerializer.instance);
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer(DATE_FORMAT));
        FASTJSON_CONFIG.setSerializerFeatures(SERIALIZER_FEATURES);
        FASTJSON_CONFIG.setSerializeConfig(serializeConfig);
    }

    /**
     * 拷贝bean 浅拷贝
     * 
     * @param source
     * @param targetClass
     * @return
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        Class<? extends Object> sourceClass = source.getClass();
        ConcurrentHashMap<Class<?>, BeanCopier> beanCopierMap = beanCopierCache.get(sourceClass);
        if (beanCopierMap == null) {
            beanCopierCache.putIfAbsent(sourceClass, new ConcurrentHashMap<>());
            beanCopierMap = beanCopierCache.get(sourceClass);
        }
        BeanCopier copier = beanCopierMap.get(targetClass);
        if (copier == null) {
            beanCopierMap.putIfAbsent(targetClass, BeanCopier.create(source.getClass(), targetClass, false));
            copier = beanCopierMap.get(targetClass);
        }
        T targetObj;
        try {
            targetObj = targetClass.newInstance();
            copier.copy(source, targetObj, null);
            return targetObj;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(targetClass.getName() + " could not initial ! ", e);
        }
    }


    /**
     * bean列表复制
     * 
     * @param sourceList
     * @param targetClass
     * @return
     */
    public static <T, E> List<T> copy(List<E> sourceList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Lists.emptyList();
        }
        return sourceList.parallelStream().map(sourceObj -> copy(sourceObj, targetClass)).collect(Collectors.toList());
    }


    /**
     * 深copy
     * 
     * @param source
     * @param targetClass
     * @return
     */
    public static <T> T deepCopy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }

        return JSON.parseObject(JSON.toJSONString(source), targetClass);
    }


    /**
     * 
     * @param sourceList
     * @param targetClass
     * @return
     */
    public static <T, E> List<T> deepCopy(List<E> sourceList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Lists.emptyList();
        }

        return JSON.parseArray(JSON.toJSONString(sourceList), targetClass);
    }


    /**
     * 将对象转换成Map
     * 
     * @param bean
     * @return map ，key为属性名，如果属性为复杂类型，则为嵌套map
     */
    public static Map<String, Object> bean2Map(Object bean) {
        Map<String, Object> map = new HashMap<>(1);
        BeanMap beanMap = BeanMap.create(bean);
        Set<?> set = beanMap.keySet();
        for (Object key : set) {
            Object value = beanMap.get(key);
            bean2MapRecursion(map, value, key.toString());
        }

        return map;
    }


    private static void bean2MapRecursion(Map<String, Object> map, Object bean, String propertyName) {
        if (bean != null) {
            try {
                if (isPrimitive(bean.getClass())) {
                    map.put(propertyName, bean);
                } else if (Object[].class.isInstance(bean)) {
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (Object item : (Object[]) bean) {
                        list.add(bean2Map(item));
                    }
                    map.put(propertyName, list);
                } else if (Collection.class.isInstance(bean)) {
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (Object item : (Collection<?>) bean) {
                        list.add(bean2Map(item));
                    }
                    map.put(propertyName, list);
                } else if (Map.class.isInstance(bean)) {
                    Map<String, Object> subMap = new HashMap<>();
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) bean).entrySet()) {
                        bean2MapRecursion(subMap, entry.getValue(), entry.getKey().toString());
                    }
                    map.put(propertyName, subMap);
                } else {
                    Map<String, Object> subMap = new HashMap<>();
                    BeanMap beanMap = BeanMap.create(bean);
                    Set<?> set = beanMap.keySet();
                    for (Object key : set) {
                        Object value = beanMap.get(key);
                        bean2MapRecursion(subMap, value, key.toString());
                    }
                    map.put(propertyName, subMap);
                }
            } catch (RuntimeException e) {
                log.error("propertyName = {} , value = ", propertyName, bean);
                throw e;
            }
        }
    }


    /**
     * 是否基本类型
     * 
     * @param cls
     * @return
     */
    public static boolean isPrimitive(Class<?> cls) {
        if (cls.isArray()) {
            return isPrimitive(cls.getComponentType());
        }

        return cls.isPrimitive() || cls == Class.class || cls == String.class || cls == Boolean.class || cls == Character.class || Number.class.isAssignableFrom(cls)
                || Date.class.isAssignableFrom(cls);
    }


    /**
     * 
     * @param obeject
     * @return
     */
    public static String toJsonString(Object obeject) {
        return JSON.toJSONString(obeject, SERIALIZER_FEATURES);
    }


    /**
     * json文本转换成 java对象
     * 
     * @param jsonString
     * @param targetClass
     * @return
     */
    public static <T> T parseObject(String jsonString, Class<T> targetClass) {
        return JSON.parseObject(jsonString, targetClass, FASTJSON_CONFIG.getFeatures());
    }


    /**
     * json文本转换成List java对象
     * 
     * @param jsonString
     * @param targetClass
     * @return
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> targetClass) {
        return JSON.parseArray(jsonString, targetClass);
    }
}
