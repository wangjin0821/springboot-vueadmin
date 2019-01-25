package com.wiggin.mangersys.util.apifeignclient.eccang.bean;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class EcProductResponse {
	
	private String productSku;
	private String productSpu;
	
	private String productTitle;
	private String productTitleEn;
	
	private BigDecimal productDeclaredValue;
	private String pdDeclareCurrencyCode;
	
	private BigDecimal productWeight;
	private BigDecimal productNetWeight;
	
	private String defaultSupplierCode;
	
	private Integer saleStatus;
	
	private BigDecimal productLength;
	private BigDecimal productWidth;
	private BigDecimal productHeight;
	
	private Integer designerId;
	private Integer personOpraterId;
	private Integer personSellerId;
	private Integer personDevelopId;
	
	private Integer isQc;
	private Integer isExpDate;
	private Integer isGift;
	
	private String warehouseBarcode;
	
	private Date productAddTime;
	private Date productUpdateTime;
	
	private Integer isCombination;
	
	private Integer productSizeId;
	private Integer productColorId;
	
	private String puName;
	private Integer defaultWarehouseId;
	
	private String eanCode;
	private Integer userOrganizationId;
	private Integer prl_id;
	
	private Integer oprationType; // 运营方式：1代运营、2自运营
	
	private Integer procutCategoryCode1;
	private String procutCategoryName1;
	private Integer procutCategoryCode2;
	private String procutCategoryName2;
	private Integer procutCategoryCode3;
	private String procutCategoryName3;
	
	private BigDecimal sp_unit_price;	// 供应商产品单价
	private String currency_code; // 供应商币种
	
	private String brandCode;
	private String brandName;
	
	/**
	 * 产品主图url
	 */
	private String mainImg;
}
