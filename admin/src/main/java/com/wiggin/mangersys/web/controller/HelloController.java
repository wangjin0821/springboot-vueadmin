package com.wiggin.mangersys.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiggin.mangersys.domain.entity.ApiUser;

@RestController
@RequestMapping("/hello")
public class HelloController {
		
	@GetMapping("/index")
	public String hello() {
		return "Hello, index!!!";
	}
	
	@PostMapping("/test")
	public ApiUser test() {
		ApiUser apiUser = new ApiUser();
		apiUser.setUserName("test");
		return apiUser;
	}
}
