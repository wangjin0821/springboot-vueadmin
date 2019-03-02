package com.wiggin.mangersys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class ApplicationProperties {

    @Value("${product.pictureHost}")
    public String pictureHost;

    @Value("${product.picParseDir}")
    public String picParseDir;

    @Value("${product.picExtends}")
    public String picExtends;
    
    @Value("${product.picDir}")
    public String picDir;
}
