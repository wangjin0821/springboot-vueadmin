package com.wiggin.mangersys.web.vo.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageRequest {
	
	@Autowired
	private Pagination pagination;
	
	@ApiModelProperty("sku")
	private List<String> sku;
	
	@ApiModelProperty("销售状态")
	private List<Integer> saleStatus;
	
	@ApiModelProperty("一级分类")
	private Integer category1;
	
	@ApiModelProperty("二级分类")
	private Integer category2;
}
