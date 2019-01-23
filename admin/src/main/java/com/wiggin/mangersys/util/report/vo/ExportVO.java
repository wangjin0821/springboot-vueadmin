package com.wiggin.mangersys.util.report.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 导出列
 * 
 * @author weiem
 *
 */
@Data
public class ExportVO implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "导出字段名")
    @NotNull(message = "导出字段名不能为空")
    private String title;

    @ApiModelProperty(value = "导出字段映射值")
    @NotNull(message = "导出字段映射值不能为空")
    private String field;

    @ApiModelProperty(value = "列是否汇总")
    private boolean columnSum;
}
