package com.wiggin.mangersys.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.wiggin.mangersys.config.BusinessException;
import com.wiggin.mangersys.config.ExceptionCodeEnum;
import com.wiggin.mangersys.domain.entity.UserRoles;
import com.wiggin.mangersys.domain.mapper.UserRolesMapper;
import com.wiggin.mangersys.service.UserRolesService;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
@Service
@Slf4j
public class UserRolesServiceImpl implements UserRolesService {

    @Autowired
    private UserRolesMapper userRoleMapper;


    @Override
    public List<UserRoles> getUserRolesByUserId(Integer userId) {
        UserRoles userRoles = new UserRoles();
        userRoles.setUserId(userId);
        Wrapper<UserRoles> wrapper = new EntityWrapper<>(userRoles);
        List<UserRoles> userRoleList = userRoleMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(userRoleList)) {
            return Lists.emptyList();
        }
        return userRoleList;
    }


    @Override
    @Transactional
    public Integer saveUserRoles(List<UserRoles> userRoleList) {
        if (CollectionUtils.isEmpty(userRoleList)) {
            throw new BusinessException(ExceptionCodeEnum.USER_ROLE_IS_EMPTY);
        }
        log.info("userRoleList=>{}", userRoleList);
        Set<Integer> userIdSet = userRoleList.parallelStream().map(UserRoles::getUserId).collect(Collectors.toSet());
        Set<Integer> roleIdSet = userRoleList.parallelStream().map(UserRoles::getRoleId).collect(Collectors.toSet());
        Wrapper<UserRoles> wrapper = new EntityWrapper<>();
        wrapper.in("user_id", userIdSet);
        wrapper.in("role_id", roleIdSet);
        userRoleMapper.delete(wrapper);
        
        return userRoleMapper.batchInsert(userRoleList);
    }

}
