package com.wiggin.mangersys.service;

import java.util.List;

import com.wiggin.mangersys.domain.entity.RolePermission;

/**
 * <p>
 * 角色权限关联表 服务类
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
public interface RolePermissionService {
    
    Integer saveRolePermission(List<RolePermission> rolePermissionList);
    
    List<RolePermission> getRolePermissionListByRoleId(Integer roleId);
}
