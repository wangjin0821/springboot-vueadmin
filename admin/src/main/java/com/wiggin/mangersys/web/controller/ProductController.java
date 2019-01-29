package com.wiggin.mangersys.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiggin.mangersys.domain.entity.Product;
import com.wiggin.mangersys.service.ProductService;
import com.wiggin.mangersys.util.BeanUtil;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductCategoryResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductSaleStatusResponse;
import com.wiggin.mangersys.util.report.BaseExport;
import com.wiggin.mangersys.web.vo.request.ProductPageRequest;
import com.wiggin.mangersys.web.vo.response.ProductPageResponse;


/**
 * <p>
 * 产品主信息表 前端控制器
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
@RestController
@RequestMapping("/product")
public class ProductController implements BaseExport {

    @Autowired
    private ProductService productService;


    @PostMapping("/list")
    public Page<ProductPageResponse> getProductList(@RequestBody ProductPageRequest productReq) {
        return productService.getProductList(productReq);
    }


    @PostMapping("/add")
    public void add(@RequestBody Product product) {
        productService.saveProduct(product);
    }


    @PostMapping("/delete")
    public void delete(@RequestParam("id") Integer id) {
        productService.deleteProduct(id);
    }


    @GetMapping("/syncProductList")
    public Integer syncProductList() {
        return productService.syncProductList();
    }


    @GetMapping("/getCategoryList")
    public List<EcProductCategoryResponse> getCategoryList() {
        return productService.getCategoryList();
    }


    @GetMapping("/getSaleStatusList")
    public List<EcProductSaleStatusResponse> getSaleStatusList() {
        return productService.getSaleStatusList();
    }


    @GetMapping("/syncProductMainImage")
    public Integer syncProductMainImage(@RequestParam(name = "sku", required = false) String sku) {
        return productService.syncProductMainImage(sku);
    }


    @GetMapping("/parseProductLocalImage")
    public Integer parseProductLocalImage() {
        return productService.parseProductLocalImage();
    }


    @Override
    public Page<?> getExportList(Map<String, Object> parameter) {
        ProductPageRequest productReq = BeanUtil.deepCopy(parameter, ProductPageRequest.class);
        return getProductList(productReq);
    }
}
