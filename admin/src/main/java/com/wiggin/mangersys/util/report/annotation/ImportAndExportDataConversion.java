package com.wiggin.mangersys.util.report.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

import com.alibaba.druid.util.lang.Consumer;


/**
 * 数据转换注解
 * 
 * @author weiem
 *
 */
@Target({ java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportAndExportDataConversion {

    /**
     * 导入导出数据转换class
     * 
     * @return
     */
    Class<?> conversionClass() default Class.class;


    /**
     * 通过字典类型获取字段中的值来进行转换
     * 
     * @return
     */
    String dicType() default "";


    /**
     * 通过接口方法获取转换数据
     * 
     * @return
     */
    String method() default "";


    /**
     * 获取的数据的名字
     * 
     * @return
     */
    String name() default "";


    /**
     * 将数据名字转换成值
     * 
     * @return
     */
    String value() default "";
    
    
    /**
     * 要转换数据的转换接口
     * @return
     */
    boolean specialDataConvert() default false;
}
