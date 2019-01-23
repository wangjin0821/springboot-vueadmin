package com.wiggin.mangersys.web.vo.response;

import java.util.List;

import lombok.Data;

@Data
public class TreeBaseResponse {

    private Integer id;
    private Integer parentId;
    
    private List<? extends TreeBaseResponse> children;
}
