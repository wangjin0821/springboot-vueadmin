package com.wiggin.mangersys.domain.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@TableName("api_roles")
public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer parentId;

    /**
     * 角色名
     */
    @NotEmpty
    private String roleName;
    /**
     * 角色code
     */
    @NotEmpty
    private String roleCode;
    private Date createTime;
    private Date modifyTime;


}
