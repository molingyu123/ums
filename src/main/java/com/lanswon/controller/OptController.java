package com.lanswon.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanswon.service.OptService;

@Controller
@RequestMapping("/opt")
public class OptController {
    @Resource
    private OptService optService;

    @RequestMapping("/system/addOpt")
    @ResponseBody
    public Object addOpt(@RequestParam Map<String, Object> map) {
        return optService.addOpt(map);
    }

    @RequestMapping("/system/delOpt")
    @ResponseBody
    public Object delOpt(String id) {
        return optService.delOpt(id);
    }


    @RequestMapping("/system/findAllOpt")
    @ResponseBody
    public Object findAllOpt() {
        return optService.findAllOpt();
    }

    @RequestMapping("/system/findAllOptByUserId")
    @ResponseBody
    public Object findAllOptByUserGId(String token) {
        return optService.findAllOptByUserGId(token);
    }


    @RequestMapping("/system/addOptQX")
    @ResponseBody
    public Object addOptQX(@RequestParam Map<String, Object> map) {
        return optService.addOptQX(map);
    }

    @RequestMapping("/system/delOptQX")
    @ResponseBody
    public Object delOptQX(String id) {
        return optService.delOptQX(id);
    }

    @RequestMapping("/system/checkUserQX")
    @ResponseBody
    public Object checkUserQX(String userGId, String[] optId) {
        return optService.checkUserQX(userGId, optId);
    }
}
