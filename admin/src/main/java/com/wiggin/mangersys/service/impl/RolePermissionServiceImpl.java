package com.wiggin.mangersys.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.google.common.collect.Sets;
import com.wiggin.mangersys.domain.entity.RolePermission;
import com.wiggin.mangersys.domain.mapper.RolePermissionMapper;
import com.wiggin.mangersys.service.MenuService;
import com.wiggin.mangersys.service.RolePermissionService;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 角色权限关联表 服务实现类
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
@Service
@Slf4j
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private MenuService menuService;


    @Override
    public Integer saveRolePermission(List<RolePermission> rolePermissionList) {
        log.info("rolePermissionList => {}", rolePermissionList);
        if (CollectionUtils.isEmpty(rolePermissionList)) {
            return 0;
        }
        List<Integer> menuIds = Lists.newArrayList();
        Set<Integer> roleIds = Sets.newConcurrentHashSet();
        for (RolePermission rolePermission : rolePermissionList) {
            menuIds.add(rolePermission.getResouceId());
            roleIds.add(rolePermission.getRoleId());
        }
        Set<Integer> allMenuIds = menuService.getAllParentListByIds(menuIds);
        log.info("allMenuIds => {}", allMenuIds);
        for (Integer menuId : allMenuIds) {
            for (Integer roleId : roleIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setResouceId(menuId);
                rolePermission.setResouceType(1);
                Wrapper<RolePermission> wrapper = new EntityWrapper<>(rolePermission);
                Integer count = rolePermissionMapper.selectCount(wrapper);
                if (count > 0) {
                    continue;
                }
                rolePermissionMapper.insert(rolePermission);
            }
        }
        
        return 1;
    }


    @Override
    public List<RolePermission> getRolePermissionListByRoleId(Integer roleId) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        Wrapper<RolePermission> wrapper = new EntityWrapper<>(rolePermission);
        return rolePermissionMapper.selectList(wrapper);
    }

}
