package com.wiggin.mangersys.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.wiggin.mangersys.domain.entity.Roles;
import com.wiggin.mangersys.domain.mapper.RolesMapper;
import com.wiggin.mangersys.service.RolesService;
import com.wiggin.mangersys.util.TreeUtil;
import com.wiggin.mangersys.web.vo.request.RoleSaveRequest;
import com.wiggin.mangersys.web.vo.response.RoleTreeResponse;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
@Service
@Slf4j
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesMapper roleMapper;


    @Override
    public List<RoleTreeResponse> getRolesTree() {
        Wrapper<Roles> wrapper = new EntityWrapper<>();
        List<Roles> roleList = roleMapper.selectList(wrapper);
        log.info("roleList=>{}", roleList);
        if (CollectionUtils.isEmpty(roleList)) {
            return Lists.emptyList();
        }
        return TreeUtil.createTree(RoleTreeResponse.class, roleList, 0, Roles::getParentId, roles -> (roles.getId()),
            (RoleTreeResponse roleResponse, List<RoleTreeResponse> roleResponseList) -> {
                roleResponse.setChildren(roleResponseList);
            });
    }


    @Override
    public Integer deleteRoles(List<Integer> idList) {
        return roleMapper.deleteBatchIds(idList);
    }


    @Override
    public Integer saveRoles(RoleSaveRequest roleReq) {
        Roles roleEntity = new Roles();
        BeanUtils.copyProperties(roleReq, roleEntity);
        if (roleEntity.getId() != null && roleEntity.getId() > 0) {
            return roleMapper.updateById(roleEntity);
        } else {
            return roleMapper.insert(roleEntity);
        }

    }

}
