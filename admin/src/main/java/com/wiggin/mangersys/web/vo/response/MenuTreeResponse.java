package com.wiggin.mangersys.web.vo.response;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MenuTreeResponse implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3202672141541218001L;
    private Integer id;
    private Integer parentId;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 菜单路径
     */
    private String path;
    /**
     * 图标的class
     */
    private String icon;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 是否显示
     */
    private String isShow;

    /**
     * 子节点
     */
    private List<MenuTreeResponse> children;
}
