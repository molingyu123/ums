package com.lanswon.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanswon.service.MenuService;
import com.lanswon.util.PageHelper;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @RequestMapping("/system/queryAllMenus")
    @ResponseBody
    public Object queryAllMenus(@RequestParam Map<String, Object> map) {
        return menuService.queryAllMenus(PageHelper.mapToMetaData(map));
    }

    @RequestMapping("/system/queryFirstLevelMenu")
    @ResponseBody
    public Object queryFirstLevelMenu(@RequestParam Map<String, Object> map) {
        return menuService.queryFirstLevelMenu(PageHelper.mapToMetaData(map));
    }


    @RequestMapping("/system/querySubMenu")
    @ResponseBody
    public Object querySubMenu(@RequestParam Map<String, Object> map) {
        return menuService.querySubMenuByNodeId(PageHelper.mapToMetaData(map));
    }


    /**
     * @param roleId
     * @param page
     * @return
     */
    @RequestMapping("/system/queryFirstLevelMenuByRoleId")
    @ResponseBody
    public Object queryFirstLevelMenuByRoleId(String roleId, String page, String pageSize) {
        return menuService.queryFirstLevelMenuByRoleId(roleId, Integer.parseInt(page), Integer.parseInt(pageSize));
    }

    @RequestMapping("/system/queryFirstLevelMenuByUserId")
    @ResponseBody
    public Object queryFirstLevelMenuByUserId(String userId) {
        return menuService.queryFirstLevelMenuByUserId(userId);
    }

    @RequestMapping("/system/queryAllMenuByRoleId")
    @ResponseBody
    public Object queryAllMenuByRoleId(String token) {
        return menuService.queryAllMenuByRoleId(token);
    }

    @RequestMapping("/system/querySubMenuByGId")
    @ResponseBody
    public Object querySubMenuByGId(String nodeId, String usergroupId, String page, String pageSize) {
        return menuService.querySubMenuByGId(nodeId, usergroupId, Integer.parseInt(page), Integer.parseInt(pageSize));
    }

    /**
     * @param nodepid选填
     * @param page选填
     * @param token
     * @return
     */
    @RequestMapping("/system/queryMenus")
    @ResponseBody
    public Object queryMenus(@RequestParam Map<String, Object> map) {
        return menuService.queryMenus(PageHelper.mapToMetaData(map));
    }

    @RequestMapping("/system/querySubMenuByUserId")
    @ResponseBody
    public Object querySubMenuByUserId(String userId, String nodeId, String page) {
        return menuService.querySubMenuByUserId(nodeId, userId, Integer.parseInt(page));
    }

    @RequestMapping("/system/queryAllSubMenusByUserId")
    @ResponseBody
    public Object queryAllSubMenusByUserId(String userId) {
        return menuService.queryAllSubMenusByUserId(userId);
    }

    //传入USERGROUPID和MENUID
    @RequestMapping("/system/addMenuQX")
    @ResponseBody
    public Object addMenuQX(@RequestParam Map<String, Object> map) {
        return menuService.addMenuQX(map);
    }

    @RequestMapping("/system/delMenuQX")
    @ResponseBody
    public Object delMenuQX(String menuId, String userGroupId) {
        return menuService.delMenuQX(menuId, userGroupId);
    }

    //需要传入上一级菜单的ID(参数名PID)，若没有上一级菜单则不传
    @RequestMapping("/system/addMenu")
    @ResponseBody
    public Object addMenu(@RequestParam Map<String, Object> map) {
        return menuService.addMenu(map);
    }

    @RequestMapping("/system/updateMenu")
    @ResponseBody
    public Object updateMenu(@RequestParam Map<String, Object> map) {
        return menuService.updateMenu(map);
    }

    @RequestMapping("/system/delMenu")
    @ResponseBody
    public Object delMenu(String id) {
        return menuService.delMenu(id);
    }

}
