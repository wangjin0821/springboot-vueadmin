package com.wiggin.mangersys.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.google.common.collect.Sets;
import com.wiggin.mangersys.domain.entity.Menu;
import com.wiggin.mangersys.domain.mapper.MenuMapper;
import com.wiggin.mangersys.service.MenuService;
import com.wiggin.mangersys.util.TreeUtil;
import com.wiggin.mangersys.web.vo.request.MenuSaveRequest;
import com.wiggin.mangersys.web.vo.response.MenuTreeResponse;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author wiggin
 * @since 2018-09-15
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;


    @Override
    public List<MenuTreeResponse> getMenuTree() {
        Wrapper<Menu> wrapper = new EntityWrapper<>();
        List<Menu> menuList = menuMapper.selectList(wrapper);
        log.info("menuList => {}", menuList);
        if (CollectionUtils.isEmpty(menuList)) {
            return Lists.emptyList();
        }
        List<MenuTreeResponse> menuTrees = TreeUtil.createTree(MenuTreeResponse.class, menuList, 0, Menu::getParentId, menu -> (menu.getId()),
            (MenuTreeResponse menuResponse, List<MenuTreeResponse> menuResponseList) -> {
                menuResponse.setChildren(menuResponseList);
            });
        // List<MenuTreeResponse> menuTrees =
        // TreeUtil.createTree(MenuTreeResponse.class, menuList, 0,
        // Menu::getParentId);
        log.info("menuTrees => {}", menuTrees);
        return menuTrees;
    }


    @Override
    public Integer saveMenu(MenuSaveRequest menuReq) {
        Menu entity = new Menu();
        BeanUtils.copyProperties(menuReq, entity);
        if (entity.getId() != null && entity.getId() > 0) {
            return menuMapper.updateById(entity);
        } else {
            return menuMapper.insert(entity);
        }

    }


    @Override
    public Integer deleteMenu(List<Integer> menuIds) {
        return menuMapper.deleteBatchIds(menuIds);
    }


    @Override
    public Set<Integer> getAllParentListByIds(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Sets.newConcurrentHashSet();
        }
        return getParentListByIds(Sets.newConcurrentHashSet(idList));
    }
    
    
    /**
     * 
     * @param idList
     * @return
     */
    private Set<Integer> getParentListByIds(Set<Integer> idList) {
        Set<Integer> menuIds = Sets.newConcurrentHashSet();
        if (CollectionUtils.isEmpty(idList)) {
            return menuIds;
        }
        List<Menu> menuList = menuMapper.selectBatchIds(idList);
        if (CollectionUtils.isEmpty(menuList)) {
            return menuIds;
        }
        Set<Integer> parentIds = Sets.newConcurrentHashSet();
        for (Menu menu : menuList) {
            menuIds.add(menu.getId());
            if (!new Integer(0).equals(menu.getParentId())) {
                parentIds.add(menu.getParentId());
            }
        }
        menuIds.addAll(getParentListByIds(parentIds));
        return menuIds;
    }

}
