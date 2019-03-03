package com.wiggin.mangersys.web.vo.request;

import lombok.Data;

@Data
public class ProductPicSaveRequest {
    
    private Integer productId;
    
    private Integer picId;
    
    private String path;
}
