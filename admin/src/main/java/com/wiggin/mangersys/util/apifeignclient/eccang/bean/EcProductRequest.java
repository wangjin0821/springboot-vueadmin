package com.wiggin.mangersys.util.apifeignclient.eccang.bean;

import lombok.Data;

@Data
public class EcProductRequest {
	
	private String productSku;
	private String productSkuLike;
	
	private String productTitle;
	private String productTitleLike;
	
	private String productAddTimeFrom;
	private String productAddTimeTo;
	
	private String productUpdateTimeFrom;
	private String productUpdateTimeTo;
	
	private Integer isCombination;
	private Integer getProductCombination;
	
	private Integer getProductBox;
	
	private Integer getProperty;
	
	private Integer getProductCustomCategory;
	
	private Integer page;
	
	private Integer pageSize;
	
}
