package com.wiggin.mangersys.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.wiggin.mangersys.domain.entity.UserRoles;

/**
 * <p>
 * 用户角色关联表 Mapper 接口
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
public interface UserRolesMapper extends BaseMapper<UserRoles> {
    
    Integer batchInsert(@Param("userRoleList") List<UserRoles> userRoleList);
}
