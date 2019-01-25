package com.wiggin.mangersys.service;

import java.util.List;

import com.wiggin.mangersys.domain.entity.Product;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductCategoryResponse;
import com.wiggin.mangersys.util.apifeignclient.eccang.bean.EcProductSaleStatusResponse;
import com.wiggin.mangersys.web.vo.request.ProductPageRequest;
import com.wiggin.mangersys.web.vo.response.ProductPageResponse;

/**
 * <p>
 * 产品主信息表 服务类
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
public interface ProductService {
	
	Page<ProductPageResponse> getProductList(ProductPageRequest productReq);
	
	Integer saveProduct(Product product);
	
	Integer deleteProduct(Integer id);
	
	Integer syncProductList();
	
	List<EcProductCategoryResponse> getCategoryList();
	
	List<EcProductSaleStatusResponse> getSaleStatusList();
	
	Integer syncProductMainImage(String sku);
}
