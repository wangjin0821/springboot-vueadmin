package com.wiggin.mangersys.config;

import java.nio.charset.Charset;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;


@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**").allowedOrigins("*").allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE").maxAge(3600).allowCredentials(true);
    }


    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        // 1. 需要定义一个converter转换消息的对象
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();

        // 2. 添加fastjson的配置信息，比如:是否需要格式化返回的json的数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        SerializerFeature[] serializerFeatures = new SerializerFeature[] {
           // 输出key是包含双引号
           // SerializerFeature.QuoteFieldNames,
           // 是否输出为null的字段,若为null
           // 则显示该字段
           // SerializerFeature.WriteMapNullValue,
           // 数值字段如果为null，则输出为0
           SerializerFeature.WriteNullNumberAsZero,
           // List字段如果为null,输出为[],而非null
           SerializerFeature.WriteNullListAsEmpty,
           // 字符类型字段如果为null,输出为"",而非null
           SerializerFeature.WriteNullStringAsEmpty,
           // Boolean字段如果为null,输出为false,而非null
           SerializerFeature.WriteNullBooleanAsFalse,
           // Date的日期转换器
           SerializerFeature.WriteDateUseDateFormat,
           // 循环引用
           SerializerFeature.DisableCircularReferenceDetect };

        fastJsonConfig.setSerializerFeatures(serializerFeatures);
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));

        // 3. 在converter中添加配置信息
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
        return new HttpMessageConverters(converter);
    }


    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程池数
        executor.setCorePoolSize(10);
        // 最大线程
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(10);
        // 队列满，线程被拒绝执行策略
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        return executor;
    }
}
