package com.wiggin.mangersys.web.vo.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse implements Serializable {

	private static final long serialVersionUID = -1647421199556352688L;
	
	@ApiModelProperty("头像")
	private String avatar;
	
	@ApiModelProperty("名称")
	private String name;
	
	private List<String> role;
	
	private List<String> roles;
	
	private List<MenuResponse> menuList;
}
