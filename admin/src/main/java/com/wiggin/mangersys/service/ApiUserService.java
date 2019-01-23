package com.wiggin.mangersys.service;

import java.util.List;

import com.wiggin.mangersys.domain.entity.ApiUser;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.web.vo.request.ApiUserPageRequest;
import com.wiggin.mangersys.web.vo.request.UserSaveRequest;
import com.wiggin.mangersys.web.vo.response.UserInfoResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-14
 */
public interface ApiUserService {
	
	Page<ApiUser> getListByPage(ApiUserPageRequest apiUserReq);
	
	ApiUser validateUserLogin(String userName, String passWord);
	
	String refreToken(Integer id);
	
	UserInfoResponse getInfo(String token);
	
	Integer saveUser(UserSaveRequest userReq);
	
	Integer deleteUser(List<Integer> ids);
}
