package com.wiggin.mangersys.web.vo.response;

import com.wiggin.mangersys.domain.entity.Product;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductPageResponse extends Product {

	private static final long serialVersionUID = -6504543445100658721L;
	
	private String productTitle;
	
	private String productTtileEn;
	
	private String saleStatusText;
	
	private String picturePath;
	
	private String pictureUrl;
}
