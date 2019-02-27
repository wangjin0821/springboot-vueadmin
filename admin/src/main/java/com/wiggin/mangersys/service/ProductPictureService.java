package com.wiggin.mangersys.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.wiggin.mangersys.domain.entity.ProductPicture;

/**
 * <p>
 * 产品图片信息表 服务类
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
public interface ProductPictureService extends IService<ProductPicture> {
    
    public List<ProductPicture> getProductPicListById(Integer productId);
}
