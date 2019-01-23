package com.wiggin.mangersys.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;


/**
 * 获取bean工具类
 * 
 * @author weiem
 *
 */
@Configuration
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;


    @Override
    public void setApplicationContext(ApplicationContext context) {
        SpringUtil.CONTEXT = context;
    }


    public static ApplicationContext getContext() {
        return CONTEXT;
    }
}