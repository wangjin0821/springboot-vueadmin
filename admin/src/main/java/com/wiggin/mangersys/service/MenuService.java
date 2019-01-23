package com.wiggin.mangersys.service;

import java.util.List;
import java.util.Set;

import com.wiggin.mangersys.web.vo.request.MenuSaveRequest;
import com.wiggin.mangersys.web.vo.response.MenuTreeResponse;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author wiggin
 * @since 2018-09-15
 */
public interface MenuService {
    
    List<MenuTreeResponse> getMenuTree();
    
    Integer saveMenu(MenuSaveRequest menuReq);
    
    Integer deleteMenu(List<Integer> menuIds);
    
    Set<Integer> getAllParentListByIds(List<Integer> idList);
}
