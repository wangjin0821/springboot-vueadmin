package com.wiggin.mangersys.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.wiggin.mangersys.domain.entity.Product;
import com.wiggin.mangersys.domain.entity.ProductPicture;
import com.wiggin.mangersys.service.ProductPictureService;
import com.wiggin.mangersys.service.ProductService;
import com.wiggin.mangersys.util.BeanUtil;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductCategoryResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductSaleStatusResponse;
import com.wiggin.mangersys.util.report.BaseExport;
import com.wiggin.mangersys.web.vo.request.ProductPageRequest;
import com.wiggin.mangersys.web.vo.request.ProductPicSaveRequest;
import com.wiggin.mangersys.web.vo.response.ProductPageResponse;

import lombok.extern.slf4j.Slf4j;


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
@Slf4j
public class ProductController implements BaseExport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPicService;


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
    public Integer parseProductLocalImage(@RequestParam(name = "sku", required = false) String sku) {
        return productService.parseProductLocalImage(sku);
    }


    @Override
    public Page<?> getExportList(Map<String, Object> parameter) {
        log.info("parameter=>{}", parameter);
        ProductPageRequest productReq = BeanUtil.deepCopy(parameter, ProductPageRequest.class);
        productReq.setIsExport(true);
        Page<ProductPageResponse> productList = getProductList(productReq);
        List<String> skuList = (List<String>) parameter.get("sku");
        log.info("skuList=>{}", skuList);
        if (productList != null && CollectionUtils.isNotEmpty(productList.getList())) {
            Map<String, ProductPageResponse> productMap = Maps.newConcurrentMap();
            for (ProductPageResponse productResponse : productList.getList()) {
                productMap.putIfAbsent(productResponse.getProductSku(), productResponse);
            }
            List<ProductPageResponse> productListTemp = Lists.newArrayList();
            for (String sku : skuList) {
                if (productMap.containsKey(sku)) {
                    productListTemp.add(productMap.get(sku));
                }
            }
            productList.setList(productListTemp);
        }
        return productList;
    }


    @GetMapping("/getProductPic")
    public List<ProductPicture> getProductPic(@RequestParam("productId") Integer productId) {
        return productPicService.getProductPicListById(productId);
    }


    @PostMapping("/saveProductPic")
    public Integer saveProductPic(@RequestBody ProductPicSaveRequest request) {
        Product product = new Product();
        product.setId(request.getProductId());
        product.setMainPictureId(request.getPicId());
        return productService.updateProduct(product);
    }
    
    
    @PostMapping("/savePicPath")
    public Integer savePicPath(@RequestBody ProductPicSaveRequest request) {
        return productService.setProductPicPath(request);
    }
}
