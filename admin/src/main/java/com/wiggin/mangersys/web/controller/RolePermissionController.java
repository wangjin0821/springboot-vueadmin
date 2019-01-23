package com.wiggin.mangersys.web.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiggin.mangersys.domain.entity.RolePermission;
import com.wiggin.mangersys.service.RolePermissionService;

/**
 * <p>
 * 角色权限关联表 前端控制器
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
@RestController
@RequestMapping("/rolePermission")
public class RolePermissionController {
    
    @Autowired
    private RolePermissionService rolePermissionService;
        
    @PostMapping("/save")
    public Integer save(@RequestBody List<RolePermission> rolePermissionList) {
        return rolePermissionService.saveRolePermission(rolePermissionList);
    }
    
    
    @PostMapping("/getListByRoleId")
    public List<RolePermission> getListByRoleId(@RequestParam("roleId") Integer roleId) {
        return rolePermissionService.getRolePermissionListByRoleId(roleId);
    }
}

