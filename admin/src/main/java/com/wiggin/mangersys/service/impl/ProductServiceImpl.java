package com.wiggin.mangersys.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wiggin.mangersys.constant.ApplicationConstants;
import com.wiggin.mangersys.domain.entity.Product;
import com.wiggin.mangersys.domain.entity.ProductDesc;
import com.wiggin.mangersys.domain.entity.ProductPicture;
import com.wiggin.mangersys.domain.mapper.ProductDescMapper;
import com.wiggin.mangersys.domain.mapper.ProductMapper;
import com.wiggin.mangersys.domain.mapper.ProductPictureMapper;
import com.wiggin.mangersys.service.ProductPictureService;
import com.wiggin.mangersys.service.ProductService;
import com.wiggin.mangersys.util.BaseFileUtil;
import com.wiggin.mangersys.util.DateUtil;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.util.apifeignclient.eccang.EccangApi;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductCategoryResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductRequest;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductSaleStatusResponse;
import com.wiggin.mangersys.web.vo.request.ProductPageRequest;
import com.wiggin.mangersys.web.vo.response.ProductPageResponse;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 产品主信息表 服务实现类
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static List<EcProductSaleStatusResponse> productSaleStatusList;

    private static List<EcProductCategoryResponse> productCategoryList;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EccangApi eccangApi;

    @Autowired
    private ProductDescMapper productDescMapper;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ProductPictureMapper producrtPicMapper;

    @Autowired
    private ProductPictureService productPicService;


    @Override
    public Page<ProductPageResponse> getProductList(ProductPageRequest productReq) {
        log.info("productReq=>{}", productReq);
        Pagination pagination = productReq.getPagination();
        List<ProductPageResponse> selectPage = productMapper.selectProductPage(pagination, productReq);
        if (CollectionUtils.isNotEmpty(selectPage)) {
            List<EcProductSaleStatusResponse> salesStatusList = getSaleStatusList();
            Map<Integer, EcProductSaleStatusResponse> salesStatusMap = Maps.uniqueIndex(salesStatusList, EcProductSaleStatusResponse::getPSaleId);
            selectPage.forEach(item -> {
                String pictureUrl = ApplicationConstants.pictureHost + item.getPicturePath();
                item.setPictureUrl(pictureUrl);
                item.setPictureData(pictureUrl);
                if (salesStatusMap.containsKey(item.getSaleStatus())) {
                    item.setSaleStatusText(salesStatusMap.get(item.getSaleStatus()).getPSaleName());
                }
            });
        }

        log.info("getProductList => {}", selectPage);
        return new Page<>(pagination.getTotal(), pagination.getPages(), selectPage);
    }


    @Override
    public Integer saveProduct(Product product) {
        product.setDefaultValue();
        if (product.getId() != null && product.getId() > 0) {
            return productMapper.updateById(product);
        } else {
            return productMapper.insert(product);
        }
    }


    @Override
    public Integer deleteProduct(Integer id) {
        return productMapper.deleteById(id);
    }


    @Override
    public Integer syncProductList() {
        int returnCount = 0;
        EcProductRequest productReq = new EcProductRequest();
        Wrapper<Product> wrapper = new EntityWrapper<>();
        wrapper.orderBy("product_add_time", false);
        wrapper.last("LIMIT 1");
        List<Product> selectList = productMapper.selectList(wrapper);
        if (selectList != null && selectList.get(0) != null) {
            Product product = selectList.get(0);
            productReq.setProductAddTimeFrom(DateFormatUtils.format(product.getProductAddTime(), "yyyy-MM-dd HH:mm:ss"));
            // productReq.setProductAddTimeTo(new
            // Date(System.currentTimeMillis()));
        }
        Page<EcProductResponse> productPage = eccangApi.getProductList(productReq);
        if (productPage == null) {
            return returnCount;
        }
        int totalCount = productPage.getTotalCount();
        int pageSize = eccangApi.getPageSize().intValue();
        int pages = (int) Math.ceil(totalCount / pageSize);
        log.info("pages=>{}", pages);
        List<EcProductResponse> productList = productPage.getList();
        returnCount += saveEccangProductList(productList);
        for (int i = 2; i <= pages; i++) {
            productReq.setPage(i);
            Page<EcProductResponse> productPageTemp = eccangApi.getProductList(productReq);
            if (productPage == null) {
                return returnCount;
            }
            List<EcProductResponse> productResponseList = productPageTemp.getList();
            returnCount += saveEccangProductList(productResponseList);
        }
        return returnCount;
    }


    /**
     * 保存数据
     * 
     * @param productList
     * @return
     */
    private Integer saveEccangProductList(List<EcProductResponse> productList) {
        int returnCount = 0;
        if (CollectionUtils.isEmpty(productList)) {
            return returnCount;
        }

        Set<String> productSkuSet = productList.parallelStream().filter(item -> (item.getProductSku() != null)).map(item -> (item.getProductSku())).collect(Collectors.toSet());
        Wrapper<Product> wrapper = new EntityWrapper<>();
        wrapper.in("product_sku", productSkuSet);
        List<Product> productSelectList = productMapper.selectList(wrapper);
        final Map<String, Product> productMap = Maps.newConcurrentMap();
        final Map<Integer, ProductDesc> productDescMap = Maps.newConcurrentMap();
        List<Integer> productIdList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(productSelectList)) {
            for (Product product : productSelectList) {
                productIdList.add(product.getId());
                productMap.putIfAbsent(product.getProductSku(), product);
            }

            if (CollectionUtils.isNotEmpty(productIdList)) {
                Wrapper<ProductDesc> prodescWrapper = new EntityWrapper<>();
                prodescWrapper.in("product_id", productIdList);
                List<ProductDesc> productDescList = productDescMapper.selectList(prodescWrapper);
                if (CollectionUtils.isNotEmpty(productDescList)) {
                    for (ProductDesc productDesc : productDescList) {
                        productDescMap.putIfAbsent(productDesc.getProductId(), productDesc);
                    }
                }
            }
        }

        List<List<EcProductResponse>> productListPartition = Lists.partition(productList, 100);
        for (List<EcProductResponse> eccangProductList : productListPartition) {
            log.info("eccangProductList.size=>{}", eccangProductList.size());

            // 多线程插入数据
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    for (EcProductResponse eccangProductResponse : eccangProductList) {
                        Product product = new Product();
                        BeanUtils.copyProperties(eccangProductResponse, product);
                        String productSku = product.getProductSku();
                        product.setSpUnitPrice(eccangProductResponse.getSp_unit_price());
                        product.setSpCurrencyCode(eccangProductResponse.getCurrency_code());
                        Integer productId = null;
                        if (productMap.containsKey(productSku)) {
                            Product selectOne = productMap.get(productSku);
                            productId = selectOne.getId();
                            product.setId(selectOne.getId());
                            product.setUpdateTime(DateUtil.currentTime());
                            productMapper.updateById(product);
                        } else {
                            product.setDefaultValue();
                            Integer insert = productMapper.insert(product);
                            if (insert > 0) {
                                productId = product.getId();
                            }
                        }
                        if (productId != null) {
                            ProductDesc productDesc = new ProductDesc();
                            BeanUtils.copyProperties(eccangProductResponse, productDesc);
                            productDesc.setProductId(productId);
                            if (productDescMap.containsKey(productId)) {
                                ProductDesc productDescOne = productDescMap.get(productId);
                                productDesc.setId(productDescOne.getId());
                                productDescMapper.updateById(productDesc);
                            } else {
                                productDescMapper.insert(productDesc);
                            }
                        }
                    }
                }
            });
        }
        return returnCount;
    }


    @Override
    public List<EcProductCategoryResponse> getCategoryList() {
        if (CollectionUtils.isEmpty(productCategoryList)) {
            productCategoryList = eccangApi.getProductCategoryBase();
        }
        return productCategoryList;
    }


    @Override
    public List<EcProductSaleStatusResponse> getSaleStatusList() {
        if (CollectionUtils.isEmpty(productSaleStatusList)) {
            productSaleStatusList = eccangApi.getSaleStatus();
        }
        return productSaleStatusList;
    }


    @Override
    public Integer syncProductMainImage(String sku) {
        List<Product> productList = Lists.newLinkedList();
        if (StringUtils.isNotEmpty(sku)) {
            Product product = new Product();
            product.setProductSku(sku);
            Product selectOne = productMapper.selectOne(product);
            if (selectOne != null) {
                productList.add(selectOne);
            }
        }
        EntityWrapper<Product> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("main_picture_id", 0);
        entityWrapper.orderBy("id");
        productList = productMapper.selectList(entityWrapper);
        log.info("productList.size => {}", productList.size());
        if (CollectionUtils.isNotEmpty(productList)) {
            Date currentTime = DateUtil.currentTime();
            for (Product product : productList) {
                threadPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        EcProductResponse productBySku = eccangApi.getProductBySku(product.getProductSku());
                        if (productBySku != null && productBySku.getMainImg() != null) {
                            ProductPicture productPicture = new ProductPicture();
                            productPicture.setDefaultValue();
                            productPicture.setSku(product.getProductSku());
                            productPicture.setProductId(product.getId());
                            productPicture.setPictureUrl(productBySku.getMainImg());
                            Integer insert = producrtPicMapper.insert(productPicture);
                            if (insert > 0) {
                                product.setMainPictureId(productPicture.getId());
                                product.setUpdateTime(currentTime);
                                productMapper.updateById(product);
                            }
                        }
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return productList.size();
        }
        return null;
    }


    @Override
    // @Transactional
    public Integer parseProductLocalImage() {
        Map<String, List<File>> filePathMap = Maps.newConcurrentMap();
        File dir = new File(ApplicationConstants.picParseDir);
        BaseFileUtil.listDirectory(dir, filePathMap);
        if (filePathMap.isEmpty()) {
            return 0;
        }
        List<String> imageTypeList = Lists.newArrayList(StringUtils.split(ApplicationConstants.picExtends, ","));
        // log.info("filePathList=>{}", filePathList);
        Product productEntity = new Product();
        productEntity.setIsParse(0);
        Wrapper<Product> wrapper = new EntityWrapper<>(productEntity);
        List<Product> productList = productMapper.selectList(wrapper);
        for (Product product : productList) {
            String[] split = StringUtils.split(product.getProductSku(), "-");
            if (split[0] != null) {
                String[] split2 = StringUtils.split(split[0], ".");
                if (split2[0] != null && filePathMap.containsKey(split2[0])) {
                    List<File> fileList = filePathMap.get(split2[0]);
                    List<ProductPicture> productPictureList = Lists.newArrayList();
                    fileList.forEach(file -> {
                        String fileName = file.getName();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        if (imageTypeList.contains(suffix)) {
                            String path = file.getPath();
                            String replacePath = StringUtils.replace(path, ApplicationConstants.picParseDir, "");
                            replacePath = replacePath.replace("\\", "/");
                            ProductPicture productPicture = new ProductPicture();
                            productPicture.setProductId(product.getId());
                            productPicture.setSku(product.getProductSku());
                            productPicture.setDefaultValue();
                            productPicture.setPicturePath(replacePath);
                            productPictureList.add(productPicture);
                        }
                    });
                    product.setIsParse(1);
                    product.setParseTime(DateUtil.currentTime());
                    productMapper.updateById(product);
                    productPicService.insertBatch(productPictureList);
                    /*productPictureList.forEach(productPic -> {
                        producrtPicMapper.insert(productPic);
                    });*/
                } else {
                    product.setIsParse(2);
                    product.setParseTime(DateUtil.currentTime());
                    productMapper.updateById(product);
                }
            } else {
                log.error("sku=>{} split error", product.getProductSku());
            }
        }

        return productList.size();
    }


    @Override
    public Integer updateProduct(Product product) {
        return productMapper.updateById(product);
    }

}
