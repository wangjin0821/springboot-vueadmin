package com.wiggin.mangersys.web.vo.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponse implements Serializable {

	private static final long serialVersionUID = 3225162318547926835L;

	private String name;
	
	private String path;
	
	private String component;
	
	private Boolean leaf;
	
	private String redirect;
	
	private Map<String, String> meta;
	
	private List<MenuResponse> children;
}
