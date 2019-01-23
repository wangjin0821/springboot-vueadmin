package com.wiggin.mangersys.domain.entity;

import java.util.Date;

import com.wiggin.mangersys.util.DateUtil;

import lombok.Data;

@Data
public class BaseEntity {
	
	private Date createTime;
	
	private Date updateTime;
	
	public void setDefaultValue() {
		Date date = DateUtil.currentTime();
		this.createTime = date;
		this.updateTime = date;
	}
}
