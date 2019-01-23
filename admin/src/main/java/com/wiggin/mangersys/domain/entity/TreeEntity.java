package com.wiggin.mangersys.domain.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

@Data
public class TreeEntity {
	
	@TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer parentId;
}
