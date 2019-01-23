package com.wiggin.mangersys.web.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiggin.mangersys.domain.entity.ApiUser;
import com.wiggin.mangersys.service.ApiUserService;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.web.vo.request.ApiUserPageRequest;
import com.wiggin.mangersys.web.vo.request.BatchIdRequest;
import com.wiggin.mangersys.web.vo.request.UserSaveRequest;
import com.wiggin.mangersys.web.vo.response.UserInfoResponse;

import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-14
 */
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private ApiUserService apiUserService;
	
	
	@PostMapping(value = "/list")
	@ApiOperation("用户列表")
	public Page<ApiUser> getUserList(@RequestBody ApiUserPageRequest apiUserReq) {
		return apiUserService.getListByPage(apiUserReq);
	}
	
	
	@PostMapping("/login")
	@ApiOperation(value = "login", notes = "note", produces = "application/json;charset=UTF-8")
	public String login(@RequestParam("userName") String userName, @RequestParam("passWord") String passWord) {
		ApiUser user = apiUserService.validateUserLogin(userName, passWord);
		return apiUserService.refreToken(user.getId());
	}
	
	
	@GetMapping("/info")
	public UserInfoResponse info(@RequestParam("token") String token) {
		return apiUserService.getInfo(token);
	}
	
	
	@PostMapping("/logout")
	public void logout() {
	    
	}
	
	
	@PostMapping("/save")
	public Integer save(@Valid @RequestBody UserSaveRequest userReq) {
	    return apiUserService.saveUser(userReq);
	}
	
	
	@PostMapping("/delete")
	public Integer delete(@RequestBody BatchIdRequest idsReq) {
	    List<Integer> ids = idsReq.getIds();
	    return apiUserService.deleteUser(ids);
	}
}

