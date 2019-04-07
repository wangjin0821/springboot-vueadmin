package com.wiggin.mangersys.web.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.wiggin.mangersys.service.ProductPictureService;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 产品图片信息表 前端控制器
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
@Controller
@RequestMapping("/productPicture")
public class ProductPictureController {
    
    @Autowired
    private ProductPictureService productPicService;
    
    @GetMapping("/downloadSkuPic")
    public void downloadSkuPic(@RequestParam("sku") String sku, HttpServletResponse response) {
        productPicService.downloadPicBySkus(Lists.newArrayList(sku), HttpServletResponse response);
    }
}

