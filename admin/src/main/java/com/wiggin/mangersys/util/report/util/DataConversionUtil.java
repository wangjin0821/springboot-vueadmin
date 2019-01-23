package com.wiggin.mangersys.util.report.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import com.google.common.collect.Maps;
import com.wiggin.mangersys.constant.BaseEnum;
import com.wiggin.mangersys.constant.ExportAndImportEnum;
import com.wiggin.mangersys.util.report.annotation.ImportAndExportDataConversion;


public class DataConversionUtil {


    /**
     * 获取需要转换的数据
     * 
     * @param export
     * 
     * @param clazz
     * @return 返回map<字段, map<字段值, 字段值需要转换的值>>
     * @throws Exception
     */
    public static Map<String, Map<String, String>> getDataConversion(Object object, ExportAndImportEnum exportAndImportEnum, String token) throws Exception {
        return getDataConversion(object.getClass(), exportAndImportEnum, token);
    }


    /**
     * 获取需要转换的数据
     * 
     * @param clazz
     * @return 返回map<字段, map<字段值, 字段值需要转换的值>>
     * @throws Exception
     */
    public static Map<String, Map<String, String>> getDataConversion(Class<?> clazz, ExportAndImportEnum exportAndImportEnum, String token) throws Exception {
        Map<String, Map<String, String>> fTypeMap = Maps.newHashMap();
        Field[] fields = clazz.getDeclaredFields();

        Map<String, String> fieldType = Maps.newHashMap();
        Method m = null;
        Class<?> conversionClass = null;
        for (Field field : fields) {
            /*
             * 如果实体上的注解ImportAndExportDataConversion
             * 不为空则获取ImportAndExportDataConversion的数据
             */
            if (field.getAnnotation(ImportAndExportDataConversion.class) != null) {
                ImportAndExportDataConversion formatterType = field.getAnnotation(ImportAndExportDataConversion.class);
                Map<String, String> map = Maps.newHashMap();
                conversionClass = formatterType.conversionClass();
//                String dicType = formatterType.dicType();

                /*
                 * 如果conversionClass不为空则取conversionClass的数据 如果为空则取dicType字典类型的数据
                 */
                if (!StringUtils.equals(conversionClass.getName(), "java.lang.Class")) {
                    m = conversionClass.getMethod("values");
                    BaseEnum baseEnums[] = (BaseEnum[]) m.invoke(null);

                    for (BaseEnum baseEnum : baseEnums) {
                        switch (exportAndImportEnum) {
                        case EXPORT:
                            map.put(String.valueOf(baseEnum.getCode()), baseEnum.getName());
                            break;

                        case IMPORT:
                            map.put(baseEnum.getName(), String.valueOf(baseEnum.getCode()));
                            break;
                        }
                    }
                }
                /*else if (StringUtils.isNotBlank(dicType)) {
                    switch (exportAndImportEnum) {
                    case EXPORT:
                        map = getDicKeyAndValue(dicType, token);
                        break;

                    case IMPORT:
                        map = getDicValueAndKey(dicType, token);
                        break;
                    }
                }*/

                fTypeMap.put(field.getName(), map);
                fieldType.put(field.getName(), "java.lang.String");
            } else if (!Modifier.isStatic(field.getModifiers()) && StringUtils.indexOf(field.getName(), "this$") < 0) {
                String generic = field.getGenericType().toString();
                // 判断如果该属性是对象则取对象里的值
                if (StringUtils.indexOf(generic, "class java") <= -1) {
                    // 如果对象不是list对象则把对象class名字前的 "class "过滤掉
                    String className = StringUtils.substring(generic, 6, generic.length());

                    // 获取list中的对象
                    Pattern pattern = Pattern.compile("(?<=<)(\\S+)(?=>)");
                    Matcher matcher = pattern.matcher(generic);
                    while (matcher.find()) {
                        className = matcher.group();
                    }

                    if (StringUtil.isBlank(className)) {
                        continue;
                    }

                    Class<?> attributeClass = Class.forName(className);

                    Map<String, Map<String, String>> dataConversionMap = getDataConversion(attributeClass, exportAndImportEnum, token);
                    if (dataConversionMap.get("fieldType") != null) {
                        fieldType.putAll(dataConversionMap.get("fieldType"));

                        dataConversionMap.remove("fieldType");
                    }

                    fTypeMap.putAll(dataConversionMap);
                    continue;
                }

                fieldType.put(field.getName(), field.getType().getName());
            }
        }

        // 如果类没有继承父类默认是 java.lang.Object
        if (!StringUtils.equals("java.lang.Object", clazz.getSuperclass().getName())) {
            Class<?> superclass = clazz.getSuperclass();
            Map<String, Map<String, String>> dataConversionMap = getDataConversion(superclass, exportAndImportEnum, token);

            if (dataConversionMap.get("fieldType") != null) {
                fieldType.putAll(dataConversionMap.get("fieldType"));

                dataConversionMap.remove("fieldType");
            }

            fTypeMap.putAll(dataConversionMap);
        }

        fTypeMap.put("fieldType", fieldType);

        return fTypeMap;
    }


    /**
     * 获取字典
     * 
     * @param dicType
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    /*private static List<DataDictionary> getDicType(String dicType, String token) throws ClassNotFoundException, NoSuchMethodException {
        DataDictionaryService dataDictionaryService = SpringUtil.getContext().getBean(DataDictionaryService.class);

        
         * 执行getDicType方法查询数据
         
        return dataDictionaryService.getDicType(dicType, token);
    }*/


    /**
     * 将字典数据转换为map key value形式
     * 
     * @param dicType
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    /*private static Map<String, String> getDicKeyAndValue(String dicType, String token) throws ClassNotFoundException, NoSuchMethodException {
        List<DataDictionary> list = getDicType(dicType, token);

        Map<String, String> map = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(list)) {
            for (DataDictionary vo : list) {
                map.put(vo.getDicKey(), vo.getDicValue());
            }
        }

        return map;
    }*/


    /**
     * 将字典数据转换为map value key形式
     * 
     * @param dicType
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    /*private static Map<String, String> getDicValueAndKey(String dicType, String token) throws ClassNotFoundException, NoSuchMethodException {
        List<DataDictionary> list = getDicType(dicType, token);

        Map<String, String> map = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(list)) {
            for (DataDictionary vo : list) {
                map.put(vo.getDicValue(), vo.getDicKey());
            }
        }

        return map;
    }*/
}
