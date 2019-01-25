package com.wiggin.mangersys.util.apifeignclient.eccang.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class EcApiResponse implements Serializable {
    
    private static final long serialVersionUID = 166203840413484888L;
    private Integer code;
    private String ask;
    private String meesage;
    private String data;
}
