package com.wiggin.mangersys.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.wiggin.mangersys.config.ApplicationProperties;
import com.wiggin.mangersys.domain.entity.ProductPicture;
import com.wiggin.mangersys.domain.mapper.ProductPictureMapper;
import com.wiggin.mangersys.service.ProductPictureService;
import com.wiggin.mangersys.util.BaseFileUtil;
import com.wiggin.mangersys.util.report.BaseExport;


/**
 * <p>
 * 产品图片信息表 服务实现类
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
@Service
public class ProductPictureServiceImpl extends ServiceImpl<ProductPictureMapper, ProductPicture> implements ProductPictureService {

    @Autowired
    private ApplicationProperties appProperties;


    @Override
    public List<ProductPicture> getProductPicListById(Integer productId) {
        ProductPicture productPicture = new ProductPicture();
        productPicture.setProductId(productId);
        Wrapper<ProductPicture> wrapper = new EntityWrapper<>(productPicture);
        List<ProductPicture> selectList = selectList(wrapper);
        if (CollectionUtils.isNotEmpty(selectList)) {
            String pictureHost = appProperties.getPictureHost();
            for (ProductPicture productPicture2 : selectList) {
                if (StringUtils.isNotEmpty(productPicture2.getPicturePath())) {
                    productPicture2.setPictureUrl(pictureHost + productPicture2.getPicturePath());
                }
            }
        }
        return selectList;
    }


    @Override
    public void downloadPicBySkus(List<String> skuList, HttpServletResponse response) {
        if (CollectionUtils.isEmpty(skuList)) {
            return;
        }
        Wrapper<ProductPicture> wrapper = new EntityWrapper<>();
        wrapper.in("sku", skuList);
        List<ProductPicture> picList = this.selectList(wrapper);
        if (CollectionUtils.isEmpty(picList)) {
            return;
        }
        String picDir = appProperties.getPicDir();
        Map<String, List<ProductPicture>> productPicMap = picList.parallelStream().collect(Collectors.groupingBy(ProductPicture::getSku));
        List<String> picPathList = Lists.newArrayList();
        for (Entry<String, List<ProductPicture>> productPicEntry : productPicMap.entrySet()) {
            productPicEntry.getValue().forEach(item -> {
                picPathList.add(picDir + "\\" + item.getPicturePath());
            });
        }
        String zipFileName = IdWorker.getId() + ".zip";
        try {
            File zipFileName2 = BaseFileUtil.zipFileName(zipFileName, picPathList);
            String fileName = new String(zipFileName2.getName().getBytes(), "ISO8859-1");

            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition,Ajax-Download,Ajax-Download-File"); // vue的ajax请求的时候
                                                                                                                         // 默认只能接到
                                                                                                                         // ContentType
                                                                                                                         // 等部分头部信息
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setHeader("Ajax-Download", "true");
            response.setHeader("Ajax-Download-File", fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
