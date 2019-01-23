package com.wiggin.mangersys.web.vo.request;

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
	private String sku;
}
