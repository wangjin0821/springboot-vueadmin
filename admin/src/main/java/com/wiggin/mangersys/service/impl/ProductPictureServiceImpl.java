package com.wiggin.mangersys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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

}
