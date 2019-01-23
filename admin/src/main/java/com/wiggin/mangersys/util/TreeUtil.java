package com.wiggin.mangersys.util;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;


public class TreeUtil {

    /**
     * 创建树形结构(需要依赖统一父类)
     * 
     * @param clazz
     * @param dataList
     * @param key
     * @param groupFunc
     * @return
     */
    /*public static <K, T extends TreeEntity, R extends TreeBaseResponse> List<R> createTree(Class<R> clazz, List<T> dataList, K key, Function<T, K> groupFunc) {
        Map<K, List<T>> dataMap = dataList.parallelStream().collect(Collectors.groupingBy(groupFunc));
        List<R> treeList = Lists.newArrayList();
        List<T> parentList = dataMap.get(key);
        for (T parent : parentList) {
            try {
                R treeResponse = clazz.newInstance();
                BeanUtils.copyProperties(parent, treeResponse);
                if (dataMap.containsKey(parent.getId())) {
                    treeResponse.setChildren(TreeUtil.getTreeChild(MenuTreeResponse.class, dataMap.get(parent.getId()), dataMap));
                }
                treeList.add(treeResponse);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return treeList;
    }*/


    /**
     * 获取树形结构的子节点(需要依赖统一父类)
     * 
     * @param clazz
     * @param dataList
     * @param dataMap
     * @return
     */
    /*public static <K, T extends TreeEntity, R extends TreeBaseResponse> List<R> getTreeChild(Class<R> clazz, List<T> dataList, Map<K, List<T>> dataMap) {
        List<R> returnList = Lists.newArrayList();
        for (T t : dataList) {
            R treeResponse;
            try {
                treeResponse = clazz.newInstance();
                BeanUtils.copyProperties(t, treeResponse);
                if (dataMap.containsKey(t.getId())) {
                    List<R> menuChild = getTreeChild(clazz, dataMap.get(t.getId()), dataMap);
                    treeResponse.setChildren(menuChild);
                }
                returnList.add(treeResponse);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }*/


    /**
     * 创建树形结构(不需要依赖父类型)
     * 
     * @param clazz
     * @param dataList
     * @param key
     * @param groupFunc
     * @param getIdFunc
     * @param setChildFunc
     * @return
     */
    public static <K, T, R> List<R> createTree(Class<R> clazz, List<T> dataList, K key, Function<T, K> groupFunc, Function<T, K> getIdFunc, BiConsumer<R, List<R>> setChildFunc) {
        List<R> treeList = Lists.newArrayList();
        Map<K, List<T>> dataMap = dataList.parallelStream().collect(Collectors.groupingBy(groupFunc));
        List<T> parentList = dataMap.get(key);
        for (T parent : parentList) {
            try {
                R treeResponse = clazz.newInstance();
                BeanUtils.copyProperties(parent, treeResponse);
                K IdKey = getIdFunc.apply(parent);
                if (dataMap.containsKey(IdKey)) {
                    List<T> childList = dataMap.get(IdKey);
                    List<R> treeChild = TreeUtil.getTreeChild(clazz, childList, dataMap, getIdFunc, setChildFunc);
                    setChildFunc.accept(treeResponse, treeChild);
                }
                treeList.add(treeResponse);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return treeList;
    }


    /**
     * 获取树形结构的子节点
     * 
     * @param clazz
     * @param dataList
     * @param dataMap
     * @return
     */
    public static <K, T, R> List<R> getTreeChild(Class<R> clazz, List<T> dataList, Map<K, List<T>> dataMap, Function<T, K> getIdFunc, BiConsumer<R, List<R>> setChildFunc) {
        List<R> returnList = Lists.newArrayList();
        for (T t : dataList) {
            R treeResponse;
            try {
                treeResponse = clazz.newInstance();
                BeanUtils.copyProperties(t, treeResponse);
                K IdKey = getIdFunc.apply(t);
                if (dataMap.containsKey(IdKey)) {
                    List<T> subList = dataMap.get(IdKey);
                    List<R> menuChild = getTreeChild(clazz, subList, dataMap, getIdFunc, setChildFunc);
                    setChildFunc.accept(treeResponse, menuChild);
                }
                returnList.add(treeResponse);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return returnList;
    }
}
