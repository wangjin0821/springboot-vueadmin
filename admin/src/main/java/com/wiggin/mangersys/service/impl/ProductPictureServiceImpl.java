package com.wiggin.mangersys.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wiggin.mangersys.constant.ApplicationConstants;
import com.wiggin.mangersys.domain.entity.ProductPicture;
import com.wiggin.mangersys.domain.mapper.ProductPictureMapper;
import com.wiggin.mangersys.service.ProductPictureService;


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

    @Override
    public List<ProductPicture> getProductPicListById(Integer productId) {
        ProductPicture productPicture = new ProductPicture();
        productPicture.setProductId(productId);
        Wrapper<ProductPicture> wrapper = new EntityWrapper<>(productPicture);
        List<ProductPicture> selectList = selectList(wrapper);
        if (CollectionUtils.isNotEmpty(selectList)) {
            for (ProductPicture productPicture2 : selectList) {
                productPicture2.setPictureUrl(ApplicationConstants.pictureHost + productPicture2.getPicturePath());
            }
        }
        return selectList;
    }

}
