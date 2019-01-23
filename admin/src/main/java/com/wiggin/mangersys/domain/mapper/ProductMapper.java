package com.wiggin.mangersys.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.wiggin.mangersys.domain.entity.Product;
import com.wiggin.mangersys.web.vo.request.ProductPageRequest;
import com.wiggin.mangersys.web.vo.response.ProductPageResponse;


/**
 * <p>
 * 产品主信息表 Mapper 接口
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
public interface ProductMapper extends BaseMapper<Product> {
    
    List<ProductPageResponse> selectProductPage(@Param("pagination") Pagination pagination, @Param("reqeustParam") ProductPageRequest requestParam);

}
