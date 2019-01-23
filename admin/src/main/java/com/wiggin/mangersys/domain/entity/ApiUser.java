package com.wiggin.mangersys.domain.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-14
 */
@TableName("api_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiUser implements Serializable {

	private static final long serialVersionUID = -6060946933093799124L;
	
	@TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("用戶ID")
    private Integer id;
    /**
     * 用户名
     */
	@NotEmpty(message = "用户名补不能为空")
    @ApiModelProperty("用戶名称")
    private String userName;
    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String passWord;
    /**
     * 姓名
     */
    @NotEmpty(message = "姓名不能为空")
    @ApiModelProperty("姓名")
    private String name;
    /**
     * 照片
     */
    @ApiModelProperty("头像路径")
    private String avater;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String telphone;
    /**
     * 部门id
     */
    @ApiModelProperty("部门ID")
    private Integer depId;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;	
    /**
     * token
     */
    @ApiModelProperty("token")
    private String token;
    /**
     * 过期时间戳
     */
    @ApiModelProperty("token过期时间")
    private Long tokenExpire;
    
    @ApiModelProperty("创建时间")
    private Date createTime;
    
    @ApiModelProperty("修改时间")
    private Date modifyTime;
}
