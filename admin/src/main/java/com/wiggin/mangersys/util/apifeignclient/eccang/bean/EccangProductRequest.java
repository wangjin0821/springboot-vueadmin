package com.wiggin.mangersys.util.apifeignclient.eccang.bean;

import java.util.Date;

import lombok.Data;

@Data
public class EccangProductRequest {
	
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
