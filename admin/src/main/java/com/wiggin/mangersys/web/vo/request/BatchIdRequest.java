package com.wiggin.mangersys.web.vo.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class BatchIdRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5677583711868886910L;
    private List<Integer> ids;
}
