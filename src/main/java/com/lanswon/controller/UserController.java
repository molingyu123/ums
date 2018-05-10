package com.lanswon.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanswon.service.UserService;
import com.lanswon.util.PageHelper;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;


    @RequestMapping("/public/login")
    @ResponseBody
    public Object login(String arg, String password) {
        return userService.login(arg, password);
    }

    @RequestMapping("/public/ckLogin")
    @ResponseBody
    public Object ckLogin(String token) {
        return userService.ckLogin(token);
    }

    @RequestMapping("/public/logOut")
    @ResponseBody
    public Object logOut(String id) {
        return userService.logOut(id);
    }

    @RequestMapping("/public/addUser")
    @ResponseBody
    public Object addUser(@RequestParam Map<String, Object> map) {
        return userService.addUser(map);
    }

    @RequestMapping("/system/updateUserGroup")
    @ResponseBody
    public Object updateUserGroup(@RequestAttribute(value = "userId") String userId, String usergroupId) {
        return userService.updateUserGroup(userId, usergroupId);
    }


    @RequestMapping("/system/delUser")
    @ResponseBody
    public Object delUser(String id) {
        return userService.delUser(id);
    }

    @RequestMapping("/system/modifyUser")
    @ResponseBody
    public Object modifyUser(@RequestParam Map<String, Object> map) {
        return userService.modifyUser(map);
    }

    @RequestMapping("/system/findOneUser")
    @ResponseBody
    public Object findOneUser(String arg) {
        return userService.findOneUser(arg);
    }


    /**
     * 支持模糊查询
     *
     * @param userName  选填
     * @param mobile    选填
     * @param userGroup 选填
     */
    @RequestMapping("/system/findAllUser")
    @ResponseBody
    public Object findOneUser(@RequestParam Map<String, Object> map) {
        return userService.findAllUser(PageHelper.mapToMetaData(map));
    }
}
