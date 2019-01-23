package com.wiggin.mangersys.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 产品主信息表
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("api_product")
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 * sku
	 */
	private String productSku;

	/**
	 * 产品重量
	 */
	private BigDecimal productWeight;

	/**
	 * 净重
	 */
	private BigDecimal productNetWeight;

	/**
	 * 供应商code
	 */
	private String defaultSupplierCode;

	/**
	 * 销售状态
	 */
	private Integer saleStatus;

	/**
	 * 长度
	 */
	private BigDecimal productLength;
	/**
	 * 宽度
	 */
	private BigDecimal productWidth;
	/**
	 * 高度
	 */
	private BigDecimal productHeight;

	/**
	 * 产品相关人员信息
	 */
	private Integer designerId;
	private Integer personOpraterId;
	private Integer personSellerId;
	private Integer personDevelopId;

	/**
	 * 产品添加时间
	 */
	private Date productAddTime;

	/**
	 * 产品更新时间
	 */
	private Date productUpdateTime;

	private Integer procutCategoryCode1;
	private String procutCategoryName1;
	private Integer procutCategoryCode2;
	private String procutCategoryName2;
	
	/**
	 * 供应商产品单价
	 */
	private BigDecimal spUnitPrice;
	/**
	 * 供应商价格币种币种
	 */
	private String spCurrencyCode;
}
