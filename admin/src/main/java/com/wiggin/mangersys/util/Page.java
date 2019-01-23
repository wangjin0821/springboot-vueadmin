package com.wiggin.mangersys.util;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> implements Serializable {
	
	private static final long serialVersionUID = 3012566029586849803L;
	
	//总记录数
	private int totalCount;
	
	//总页数
	private int totalPage;

	//列表数据
	private List<T> list;
}
