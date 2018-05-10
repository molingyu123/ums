package com.lanswon.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanswon.service.UserGroupService;
import com.lanswon.util.PageHelper;

@Controller
@RequestMapping("/usergroup")
public class UserGroupController {

    @Resource
    private UserGroupService userGroupService;

    @RequestMapping("/system/updateUserGroup")
    @ResponseBody
    public Object updateUserGroup(@RequestParam Map<String, Object> map) {
        return userGroupService.updateUserGroup(map);
    }

    @RequestMapping("/system/addUserGroup")
    @ResponseBody
    public Object addUserGroup(@RequestParam Map<String, Object> map) {
        return userGroupService.addUserGroup(map);
    }

    @RequestMapping("/system/delUserGroup")
    @ResponseBody
    public Object delUserGroup(String id, String userGroup) {
        return userGroupService.delUserGroup(id, userGroup);
    }

    @RequestMapping("/system/findAllUserGroup")
    @ResponseBody
    public Object findAllUserGroup(@RequestParam Map<String, Object> map) {
        return userGroupService.findAllUserGroup(PageHelper.mapToMetaData(map));
    }


    @RequestMapping("/system/findGroupUsers")
    @ResponseBody
    public Object findGroupUsers(String id) {
        return userGroupService.findGroupUsers(id);
    }

}
