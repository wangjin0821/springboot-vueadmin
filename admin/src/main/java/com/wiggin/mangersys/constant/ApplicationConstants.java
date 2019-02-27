package com.wiggin.mangersys.constant;

import org.springframework.beans.factory.annotation.Value;


public class ApplicationConstants {

    @Value("product.pictureHost")
    public static String pictureHost;

    @Value("product.picParseDir")
    public static String picParseDir;
    
    @Value("product.picExtends")
    public static String picExtends;
}
