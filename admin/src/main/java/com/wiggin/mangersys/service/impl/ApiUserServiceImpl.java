package com.wiggin.mangersys.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wiggin.mangersys.config.BusinessException;
import com.wiggin.mangersys.config.ExceptionCodeEnum;
import com.wiggin.mangersys.domain.entity.ApiUser;
import com.wiggin.mangersys.domain.mapper.ApiUserMapper;
import com.wiggin.mangersys.service.ApiUserService;
import com.wiggin.mangersys.util.MD5;
import com.wiggin.mangersys.util.Page;
import com.wiggin.mangersys.web.vo.request.ApiUserPageRequest;
import com.wiggin.mangersys.web.vo.request.UserSaveRequest;
import com.wiggin.mangersys.web.vo.response.MenuResponse;
import com.wiggin.mangersys.web.vo.response.UserInfoResponse;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wiggin123
 * @since 2018-09-14
 */
@Service
@Slf4j
public class ApiUserServiceImpl implements ApiUserService {

    @Autowired
    private ApiUserMapper apiUserMapper;


    @Override
    public Page<ApiUser> getListByPage(ApiUserPageRequest apiUserReq) {
        Pagination pagination = apiUserReq.getPagination();
        ApiUser apiUser = new ApiUser();
        if (StringUtils.isNoneBlank(apiUserReq.getUserName())) {
            apiUser.setUserName(apiUserReq.getUserName());
        }
        Wrapper<ApiUser> wrapper = new EntityWrapper<>(apiUser);
        List<ApiUser> selectPage = apiUserMapper.selectPage(pagination, wrapper);
        log.info("userList=>{}", selectPage);
        for (ApiUser user : selectPage) {
            user.setPassWord(null);
            user.setToken(null);
            user.setTokenExpire(null);
        }
        return new Page<>(pagination.getTotal(), pagination.getPages(), selectPage);
    }


    @Override
    public ApiUser validateUserLogin(String userName, String passWord) {
        if (StringUtils.isAnyEmpty(userName, passWord)) {
            throw new BusinessException(ExceptionCodeEnum.USER_NAME_IS_NULL);
        }
        ApiUser entity = new ApiUser();
        entity.setUserName(userName);
        entity.setPassWord(MD5.MD5Encode(passWord));
        log.info("ApiUser =>{}", entity);
        ApiUser selectOne = apiUserMapper.selectOne(entity);
        if (selectOne == null) {
            throw new BusinessException(ExceptionCodeEnum.USER_OR_PASS_IS_ERROR);
        }
        return selectOne;
    }


    public String refreToken(Integer id) {
        ApiUser entity = new ApiUser();
        entity.setId(id);
        long currentTimeMillis = System.currentTimeMillis();
        currentTimeMillis += 60 * 60;
        String token = MD5.MD5Encode(String.valueOf(currentTimeMillis));
        entity.setTokenExpire(currentTimeMillis);
        entity.setToken(token);
        apiUserMapper.updateById(entity);
        return token;
    }


    @Override
    public UserInfoResponse getInfo(String token) {
        ApiUser apiUser = new ApiUser();
        apiUser.setToken(token);
        ApiUser selectOne = apiUserMapper.selectOne(apiUser);
        if (selectOne == null) {
            throw new BusinessException(ExceptionCodeEnum.USER_TOKEN_NOT_FOUND);
        }
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setAvatar(selectOne.getAvater());
        userInfoResponse.setName(selectOne.getName());
        userInfoResponse.setRole(Lists.newArrayList("admin"));
        userInfoResponse.setRoles(Lists.newArrayList("admins"));
        // form菜单
        MenuResponse menuResponse = new MenuResponse();
        menuResponse.setPath("/form");
        menuResponse.setLeaf(false);
        menuResponse.setComponent("Layout");

        // form 子菜单
        MenuResponse menuResponse1 = new MenuResponse();
        menuResponse1.setPath("index");
        menuResponse1.setName("Form");
        menuResponse1.setComponent("form/index");
        menuResponse1.setLeaf(true);
        HashMap<String, String> newHashMap = Maps.newHashMap();
        newHashMap.putIfAbsent("title", "表单");
        newHashMap.putIfAbsent("icon", "form");
        menuResponse1.setMeta(newHashMap);
        menuResponse.setChildren(Lists.newArrayList(menuResponse1));

        // 系统管理菜单
        MenuResponse menuResponseSys = new MenuResponse();
        menuResponseSys.setComponent("Layout");
        menuResponseSys.setLeaf(false);
        menuResponseSys.setName("System");
        menuResponseSys.setRedirect("noredirect");
        menuResponseSys.setPath("/system");
        HashMap<String, String> newHashMap1 = Maps.newHashMap();
        newHashMap1.putIfAbsent("title", "系统管理");
        newHashMap1.putIfAbsent("icon", "dashboard");
        menuResponseSys.setMeta(newHashMap1);

        // 系统子菜单
        MenuResponse menuResponseSys1 = new MenuResponse();
        menuResponseSys1.setPath("menu");
        menuResponseSys1.setName("Menu");
        menuResponseSys1.setComponent("system/menu");
        menuResponseSys1.setLeaf(true);
        HashMap<String, String> newHashMap2 = Maps.newHashMap();
        newHashMap2.putIfAbsent("title", "菜单管理");
        newHashMap2.putIfAbsent("icon", "form");
        menuResponseSys1.setMeta(newHashMap2);

        MenuResponse menuResponseSys2 = new MenuResponse();
        menuResponseSys2.setPath("user");
        menuResponseSys2.setName("User");
        menuResponseSys2.setComponent("system/user");
        menuResponseSys2.setLeaf(true);
        HashMap<String, String> newHashMap3 = Maps.newHashMap();
        newHashMap3.putIfAbsent("title", "用户管理");
        newHashMap3.putIfAbsent("icon", "form");
        menuResponseSys2.setMeta(newHashMap3);

        MenuResponse menuResponseSys3 = new MenuResponse();
        menuResponseSys3.setPath("role");
        menuResponseSys3.setName("Role");
        menuResponseSys3.setComponent("system/role");
        menuResponseSys3.setLeaf(true);
        HashMap<String, String> newHashMap4 = Maps.newHashMap();
        newHashMap4.putIfAbsent("title", "角色管理");
        newHashMap4.putIfAbsent("icon", "form");
        menuResponseSys3.setMeta(newHashMap4);

        MenuResponse menuResponseSys4 = new MenuResponse();
        menuResponseSys4.setPath("resouce");
        menuResponseSys4.setName("Resouce");
        menuResponseSys4.setComponent("system/resouce");
        menuResponseSys4.setLeaf(true);
        HashMap<String, String> newHashMap5 = Maps.newHashMap();
        newHashMap5.putIfAbsent("title", "资源管理");
        newHashMap5.putIfAbsent("icon", "form");
        menuResponseSys4.setMeta(newHashMap5);
        menuResponseSys.setChildren(Lists.newArrayList(menuResponseSys1, menuResponseSys2, menuResponseSys3, menuResponseSys4));

        // 产品菜单
        MenuResponse menuResponseProd = new MenuResponse();
        menuResponseProd.setComponent("Layout");
        menuResponseProd.setLeaf(false);
        menuResponseProd.setName("Product");
        menuResponseProd.setRedirect("noredirect");
        menuResponseProd.setPath("/product");
        HashMap<String, String> newHashMap6 = Maps.newHashMap();
        newHashMap6.putIfAbsent("title", "产品管理");
        newHashMap6.putIfAbsent("icon", "example");
        menuResponseProd.setMeta(newHashMap6);

        MenuResponse menuResponseProd1 = new MenuResponse();
        menuResponseProd1.setPath("list");
        menuResponseProd1.setName("List");
        menuResponseProd1.setComponent("product/list");
        menuResponseProd1.setLeaf(true);
        HashMap<String, String> newHashMap7 = Maps.newHashMap();
        newHashMap7.putIfAbsent("title", "产品列表");
        newHashMap7.putIfAbsent("icon", "table");
        menuResponseProd1.setMeta(newHashMap7);
        menuResponseProd.setChildren(Lists.newArrayList(menuResponseProd1));

        userInfoResponse.setMenuList(Lists.newArrayList(menuResponse, menuResponseSys, menuResponseProd));
        return userInfoResponse;
    }


    @Override
    public Integer saveUser(UserSaveRequest userReq) {
        ApiUser apiUser = new ApiUser();
        BeanUtils.copyProperties(userReq, apiUser);
        if (StringUtils.isNotBlank(apiUser.getPassWord())) {
            apiUser.setPassWord(MD5.MD5Encode(apiUser.getPassWord()));
        }
        log.info("userReq =>{}, apiUser=>{}", userReq, apiUser);
        if (apiUser.getId() != null && apiUser.getId() > 0) {
            return apiUserMapper.updateById(apiUser);
        } else {
            return apiUserMapper.insert(apiUser);
        }
    }


    @Override
    public Integer deleteUser(List<Integer> ids) {
        return apiUserMapper.deleteBatchIds(ids);
    }

}
