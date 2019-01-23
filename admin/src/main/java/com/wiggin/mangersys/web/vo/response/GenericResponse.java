package com.wiggin.mangersys.web.vo.response;

import java.io.Serializable;
import java.util.Objects;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse implements Serializable {

	private static final long serialVersionUID = 1234750759217063948L;
	
	/**
	 * 定义状态码
	 */
	private int code;
	
	/**
	 * 描述
	 */
	private String message;
	
	/**
	 * 程序数据
	 */
	private Object data;
	
	@Override
	public String toString() {
		if (Objects.isNull(this.data)) {
			this.setData(data);
		}
		
		return JSON.toJSONString(this.data);
	}
	
}
