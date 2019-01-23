package com.wiggin.mangersys.domain.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("api_product_desc")
@AllArgsConstructor
@NoArgsConstructor
public class ProductDesc implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7488627315217683599L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 * 产品ID
	 */
	private Integer productId;

	/**
	 * 中文名称
	 */
	private String productTitle;

	/**
	 * 因为名称
	 */
	private String productTitleEn;
}
