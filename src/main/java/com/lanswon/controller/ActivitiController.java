package com.lanswon.controller;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lanswon.entity.ResultMsg;
import com.lanswon.service.ActivitiService;

@Controller

public class ActivitiController {
    @Resource
    private ActivitiService activitiService;

//	@RequestMapping(value="run/releaseAssessment",method=RequestMethod.POST)
//	public Object releaseAssessment(String processKey,List<Map<String,String>> assessment) {
//		
//		return null;
//	}

    /**
     * 发布街道考核任务(批量多任务）
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "releaseAssesses", method = RequestMethod.POST)
    @ResponseBody
    public ResultMsg relStrAssesses(String id, @RequestParam(value = "strNumbers[]") String[] strNumbers) throws Exception {
        System.out.println(id);

        System.out.println(strNumbers);
//		System.out.println(Arrays.toString(businessId));
        ResultMsg res = activitiService.releaseAssessments("1", strNumbers);
        System.out.println("收到");
        return res;
    }

    /**
     * 发布街道考核任务（固定）
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "releaseAssess", method = RequestMethod.POST)
    @ResponseBody
    public ResultMsg relStrAssess(String id, @RequestParam(value = "strNumbers[]") String[] strNumbers) throws Exception {
        System.out.println(id);

        System.out.println(strNumbers);
//		System.out.println(Arrays.toString(businessId));
        ResultMsg res = activitiService.releaseAssessment("1", strNumbers);
        System.out.println("收到");
        return res;
    }

    /**
     * 查询该用户发布的流程
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "queryProcessUnfinish")
    @ResponseBody
    public ResultMsg queryProcessUnfinish(String id) throws Exception {
        ResultMsg res = activitiService.queryProcessUnfinish(id);
        System.out.println(res);
        res.setTotal(1);
        return res;
    }

    /**
     * 查询完成的流程(上级）
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "queryProcessFinish")
    @ResponseBody
    public ResultMsg queryProcessFinish(String id) throws Exception {
        ResultMsg res = activitiService.queryProcessFinish(id);
        System.out.println(res);
        res.setTotal(1);
        return res;
    }

    /**
     * 查询填报任务
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "queryTasks")
    @ResponseBody
    public ResultMsg queryTasks(String id, String sidx, String sord) throws Exception {
        ResultMsg res = activitiService.queryTasks(id, sidx, sord);
        System.out.println(res);
        res.setTotal(1);
        return res;
    }

    /**
     * 查询某人参与的所有流程
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "queryProcessJoinByUserId")
    @ResponseBody
    public ResultMsg queryProcessByUser(String id, String sidx, String sord) throws Exception {
        System.out.println(id);
        ResultMsg res = activitiService.newQueryProcessJoinByUserId(id, sidx, sord);
//		System.out.println(res);
        res.setTotal(1);
        return res;
    }


    /**
     * 提交成果填写任务
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "resultsReport")
    @ResponseBody
    public ResultMsg resultsReport(String id, String taskId, String businessId, String comment, String str_reportstatus) throws Exception {
        System.out.println(str_reportstatus);
        ResultMsg res = new ResultMsg();
        if ("已填报".equals(str_reportstatus)) {
            return new ResultMsg(0, "已填报");
        } else {

            res = activitiService.completeTask(id, taskId, businessId, comment, "");
        }
        System.out.println(res);
        return res;
    }

    /**
     * 上报任务(单个）
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "upTask")
    @ResponseBody
    public ResultMsg upTask(String id, String taskId, String businessId, String comment) throws Exception {
        System.out.println(taskId);
        ResultMsg res = activitiService.completeTask(id, taskId, businessId, comment, "");
        System.out.println(res);
        return res;
    }

    /**
     * 上报任务(多个）
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "upTasks")
    @ResponseBody
    public ResultMsg upTasks(String con) throws Exception {
        System.out.println("con " + con);


        String ass = new String(Base64.getDecoder().decode(con.getBytes()));
        System.out.println("ass " + ass);
        ResultMsg res = activitiService.upTasks(con);
        System.out.println(res);
        return res;
    }

    /**
     * 回退任务
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "backTask")
    @ResponseBody
    public ResultMsg backTask(String id, String taskId, String businessId, String comment, String outcome) throws Exception {
        System.out.println(taskId);
        ResultMsg res = activitiService.completeTask(id, taskId, businessId, comment, outcome);
        System.out.println(res);
        return res;
    }

    /**
     * 审核任务
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "completeTask")
    @ResponseBody
    public ResultMsg completeTask(String id, String taskId, String businessId, String comment, String outcome) throws Exception {
        System.out.println(taskId);
        ResultMsg res = activitiService.completeTask(id, taskId, businessId, comment, outcome);
        System.out.println(res);
        return res;
    }

    /**
     * 待修改
     */
    @RequestMapping(value = "processImg")
    @ResponseBody
    public ResultMsg processImg(String processInstanceId) throws Exception {

        ResultMsg res = activitiService.processImg(processInstanceId);
        return res;
    }

    /**
     * 查询年度指标
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "queryStrAssess")
    @ResponseBody
    public ResultMsg queryStrAssess(String id) {
        ResultMsg res = activitiService.queryStrAssess(id);
        res.setTotal(1);
        return res;
    }

    /**
     * 添加年度指标
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "addStrAssess")
    @ResponseBody
    public ResultMsg addStrAssess(@RequestParam HashMap map) {
        ResultMsg res = activitiService.addStrAssess(map);
        return res;
    }

    @RequestMapping(value = "updateStrAssess")
    @ResponseBody
    public ResultMsg updateStrAssess(@RequestParam HashMap map) {
        ResultMsg res = activitiService.updateStrAssess(map);
        return res;
    }


}
