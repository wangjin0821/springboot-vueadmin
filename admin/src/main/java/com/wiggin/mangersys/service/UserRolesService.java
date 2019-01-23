package com.wiggin.mangersys.service;

import java.util.List;

import com.wiggin.mangersys.domain.entity.UserRoles;

/**
 * <p>
 * 用户角色关联表 服务类
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
public interface UserRolesService {
    
    List<UserRoles> getUserRolesByUserId(Integer userId);
    
    Integer saveUserRoles(List<UserRoles> userRoleList);
}
