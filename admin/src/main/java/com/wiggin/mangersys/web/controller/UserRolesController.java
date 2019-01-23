package com.wiggin.mangersys.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiggin.mangersys.domain.entity.UserRoles;
import com.wiggin.mangersys.service.UserRolesService;


/**
 * <p>
 * 用户角色关联表 前端控制器
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
@RestController
@RequestMapping("/userRoles")
public class UserRolesController {

    @Autowired
    private UserRolesService userRoleService;


    @PostMapping("/list")
    public List<UserRoles> getUserRoles(@RequestParam("userId") Integer userId) {
        return userRoleService.getUserRolesByUserId(userId);
    }
    
    
    @PostMapping("/save")
    public Integer save(@RequestBody List<UserRoles> userRoleList) {
        return userRoleService.saveUserRoles(userRoleList);
    }
}
