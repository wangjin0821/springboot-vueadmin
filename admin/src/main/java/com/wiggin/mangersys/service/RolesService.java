package com.wiggin.mangersys.service;

import java.util.List;

import com.wiggin.mangersys.web.vo.request.RoleSaveRequest;
import com.wiggin.mangersys.web.vo.response.RoleTreeResponse;


/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
public interface RolesService {

    /**
     * 树形结构角色
     * 
     * @return
     */
    List<RoleTreeResponse> getRolesTree();


    /**
     * 批量删除
     * 
     * @param idList
     * @return
     */
    Integer deleteRoles(List<Integer> idList);


    /**
     * 新增、修改
     * @param roleReq
     * @return
     */
    Integer saveRoles(RoleSaveRequest roleReq);
}
