package com.wiggin.mangersys.util.apifeignclient.eccang.bean;

import java.io.Serializable;

import lombok.Data;


/**
 * 
 * @author wangjin
 *
 */
@Data
public class EcProductCategoryResponse implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1359930212736165641L;
    private Integer pcId; // 品类ID
    private Integer pcPid; // 上级品类ID
    private Integer pcLevel;

    private String pcNameEn; // 英文名称
    private String pcName;
    private String pcHsCode; // 海关品名
    private String pcShortname; // 简写
    private Integer pcSortId; // 排序ID
    private Integer userOrganizationId; // 组织机构id
    private String pcReferenceCode; // 参考码

}
