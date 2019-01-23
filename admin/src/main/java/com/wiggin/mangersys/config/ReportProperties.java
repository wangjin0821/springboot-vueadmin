package com.wiggin.mangersys.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Configuration
@ConfigurationProperties(prefix = "report")
@Data
public class ReportProperties {

    private int pageSize = 100;

    private int timeout = 0;

    private int maxSize = 3000;

    /**
     * 每次循环行数
     */
    private int numberRowsPerCycle = 10000;

    /**
     * 每个文件最大长度
     */
    private int fileMaxSize = 40000;
}
