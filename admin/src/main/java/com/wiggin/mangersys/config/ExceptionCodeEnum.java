package com.wiggin.mangersys.config;

public enum ExceptionCodeEnum {
	SUCESS(200, "成功"),
	PARAM_VALID_ERROR(3000, "参数校验错误"),
	SERVICE_ERROR(1000, "服务器错误"),
	SERVICE_EXCEPTION(2000, "服务异常"),
	
	
	// 用戶错误
	USER_NAME_IS_NULL(1001, "用户名不能为空"),
	USER_OR_PASS_IS_ERROR(1002, "用户或密码错误"),
	USER_STATUS_DISABLE(1003, "用户已经禁用"),
	USER_TOKEN_NOT_FOUND(1004, "用户未登录"),
	
	USER_ROLE_IS_EMPTY(1005, "用户角色设置不能为空"),
	
	// 导出相关异常
	EXPORT_DATA_ERROR_CODE(9001, ""),
	
	PRODUCT_NOT_FOUND(3001, "产品找不到"),
	
	;
	
	private int code;
	
	private String message;
	
	ExceptionCodeEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
