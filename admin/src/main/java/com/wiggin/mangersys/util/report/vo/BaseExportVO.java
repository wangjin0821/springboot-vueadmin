package com.wiggin.mangersys.util.report.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 导出条件
 * 
 * @author weiem
 *
 */
@Data
public class BaseExportVO implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    @ApiModelProperty(value = "导出文件名(不填默认为随机生成一个ID值)")
    private String fileName;

    @ApiModelProperty(value = "导出列信息(例:[{field:\"name\",title:\"名字\",columnSum:false}])")
    @NotNull(message = "导出列信息不能为空")
    private String list;

    @ApiModelProperty(value = "查询条件(例:查询条件name=\"1\" {\"name\":\"1\"})")
    private String parameter;

    @ApiModelProperty(value = "Jaspersoft Studio模板名称(用于制作复杂导出模板)")
    private String templateName;

    @ApiModelProperty(value = "时间格式(不传默认:yyyy-MM-dd HH:mm:ss,必须要是正确的时间格式)")
    private String dateType;

    @ApiModelProperty(value = "导出文件格式 (1:XLS,2:XLSX,3:CSV,不传默认2,注:XLS格式最大导出行数为:65536)")
    private Integer fileType;

    @ApiModelProperty(value = "是否异步(默认:false:同步,true:异步.如果false直接下载导出文件,true则是生成到我的下载中)")
    private boolean async;
}