package com.wiggin.mangersys.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiggin.mangersys.service.RolesService;
import com.wiggin.mangersys.web.vo.request.BatchIdRequest;
import com.wiggin.mangersys.web.vo.request.RoleSaveRequest;
import com.wiggin.mangersys.web.vo.response.RoleTreeResponse;


/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author wiggin123
 * @since 2019-01-17
 */
@RestController
@RequestMapping("/role")
public class RolesController {

    @Autowired
    private RolesService roleService;


    @GetMapping("/list")
    public void list() {

    }


    @GetMapping("/tree")
    public List<RoleTreeResponse> getTree() {
        return roleService.getRolesTree();
    }


    @PostMapping("/delete")
    public Integer delete(@RequestBody BatchIdRequest idsReq) {
        List<Integer> idList = idsReq.getIds();
        return roleService.deleteRoles(idList);
    }


    @PostMapping("/save")
    public Integer save(@Valid @RequestBody RoleSaveRequest roleReq) {
        return roleService.saveRoles(roleReq);
    }
}
