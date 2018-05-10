package com.lanswon.service;


import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaData;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanswon.entity.ResultMsg;

import com.lanswon.util.HttpClientHelper;


@Service
public class ActivitiService {
    @Resource
    private DbSupport dbSupport;

    /**
     * 街道考核任务发布单个服务（单个） 给多个部门
     *
     * @param processKey
     * @param assessment
     * @return
     * @throws Exception
     */
    public ResultMsg releaseAssessment(String id, String[] strNumbers) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> str = new ArrayList<MetaData>();
        String urlParam = null;
        Map<String, Object> params = new HashMap<String, Object>();
        String charset = "utf-8";
        // 测试数据
        List<Map<String, String>> val = new ArrayList<Map<String, String>>();
        Map<String, String> e = new HashMap<String, String>();
        e.put("businessId", "1");
        // 部门id
        e.put("userId", "112");
        e.put("authenticatedUserId", "xw3204110");
        val.add(e);
        params.put("processkey", "AssessProcess");
        params.put("assessments", val);
        // 发送参数转换为string
        ObjectMapper om = new ObjectMapper();
        String foo = om.writeValueAsString(params);
        String newFoo = new String(Base64.getEncoder().encode(foo.getBytes("utf-8")));

        System.out.println("----------------------+");
        urlParam = "http://192.168.44.118:8080/activiti/run/releaseAssessments";
        rm = HttpClientHelper.sendGet(urlParam, "?map=" + newFoo, charset);
        System.out.println(rm);

        System.out.println("+++++++++++------------------++++++++++");
        // 数据装入List，审核流程填入“未填报”
        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) rm.getData(), List.class);
        for (Map<String, String> li : list) {
            System.out.println(li);

        }
        return rm;
    }

    /**
     * 街道考核任务发布多个服务（上级，多个） 未完，待续。。。
     *
     * @param processKey
     * @param assessment
     * @return
     * @throws Exception
     */
    public ResultMsg releaseAssessments(String id, String[] strNumbers) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> str = new ArrayList<MetaData>();
        String urlParam = null;
        Map<String, Object> params = new HashMap<String, Object>();
        String charset = "utf-8";
        System.out.println(strNumbers.toString());
        String numString = StringUtils.join(strNumbers, ",");

/*
StringBuffer nb=new StringBuffer();
nb.append("\'");
nb.append(numString);
nb.append("\'");
*/

        System.out.println(numString);
        String sql = "select str_responsibility_util,str_number from TAB_STR_ASSESS WHERE str_number IN (" + numString + ")";
        List<MetaData> deptnames = dbSupport.find(sql, new Object[]{}, "TAB_STR_ASSESS");
        //将责任单位与指标序号保存为一个map已供查询
        Map<String, String> numdept = new HashMap<String, String>();
        for (MetaData deptname : deptnames) {
          //  numdept.put(deptname.get("str_number"), deptname.get("str_responsibility_util"));
        }

        List<Map<String, String>> val = new ArrayList<Map<String, String>>();
        for (String strNum : strNumbers) {
            Map<String, String> map = new HashMap<String, String>();


            map.put("businessId", strNum);
            map.put("userId", numdept.get(strNum));// "111,112,113,114,115,116"
            map.put("authenticatedUserId", "xw3204110");
            val.add(map);
        }

        params.put("processkey", "AssessProcess");
        params.put("assessments", val);
        // 发送参数转换为string
        ObjectMapper om = new ObjectMapper();
        String foo = om.writeValueAsString(params);
        String newFoo = new String(Base64.getEncoder().encode(foo.getBytes("utf-8")));
        System.out.println(params);
        System.out.println("----------------------+");
        urlParam = "http://192.168.44.118:8080/activiti/run/releaseAssessments";
        ResultMsg sr = HttpClientHelper.sendGet(urlParam, "?map=" + newFoo, charset);
        System.out.println(sr);

        System.out.println("+++++++++++------------------++++++++++");
        // 数据装入List，
        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) sr.getData(), List.class);
        for (Map<String, String> li : list) {
            System.out.println(li);
        }
        return sr;
    }

    /**
     * 查询该用户发布的未完成流程（上级） 待续。。。
     *
     * @param id 指定用户的id
     * @return
     * @throws Exception
     */
    public ResultMsg queryProcessUnfinish(String id) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);
        String url = "http://192.168.44.118:8080/activiti/run/queryProcessUnfinish";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        ResultMsg sr = HttpClientHelper.sendGet(url, "?" + sbParams, charset);
        System.out.println(sr);
        // 接受到数据后查询数据库对应的流程信息
        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) sr.getData(), List.class);
        // 创建放入Rows的对象
        List<MetaData> strlist = new ArrayList<MetaData>();
        for (Map<String, String> li : list) {
            System.out.println(li);
            String strNum = li.get("businessKey").split("\\.")[1];
            String taskId = li.get("taskId");

            String sql = "select * from TAB_STR_ASSESS where STR_NUMBER=?";
            MetaData strAssess = new MetaData();
            try {
                strAssess = dbSupport.findOne(sql, new Object[]{strNum}, "TAB_STR_ASSESS");
                strAssess.put("taskId", taskId);
                strlist.add(strAssess);
                rm = new ResultMsg(0, "查询该用户发布的流程查询成功");

            } catch (Exception e) {
                rm.setMsg("查询该用户发布的流程失败：" + e.getMessage());
                e.printStackTrace();
            }

        }
        rm.setRows(strlist);
        return rm;

    }

    /**
     * 查询完成的流程(上级） 完成为空，待续。。。
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ResultMsg queryProcessFinish(String id) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);

        String url = "http://192.168.44.118:8080/activiti/run/queryProcessfinish";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        ResultMsg sr = HttpClientHelper.sendGet(url, "?" + sbParams, charset);
        System.out.println("请求activiti后台 " + sr);
        // 接受到数据后查询数据库对应的流程信息
        List<Map<String, String>> listNew = new ArrayList<>();

        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) sr.getData(), List.class);

        // 查询部门号对应的部门名
        String sql = "select DEPT_NAME,DEPT_NUMBER from TAB_USER WHERE DEPT_NUMBER IN ('111','112','113','114','115')";
        List<MetaData> deptnames = dbSupport.find(sql, new Object[]{}, "TAB_USER");
        System.out.println("部门对应号 " + deptnames);
        // 循环设置部门名
        for (Map<String, String> li : list) {
            System.out.println(li);
            String strNum = li.get("businessKey").split("\\.")[1];
            String taskId = li.get("taskId");
            // 通过上面获取的部门名,部门号的值循环查询当前部门号对应的部门名：
            // String detpname = searchDeptName(strNum);
            String detpname = "模拟数据";
            li.put("detpname", detpname);
            listNew.add(li);
        }
        return rm;

    }

    /**
     * 查询考核任务（下级，上级）
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ResultMsg queryTasks(String id, String sidx, String sord) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        //将部门与用户存入一个Map已备查询————————+
        Map<String, String> namedept = new HashMap<String, String>();
        String nameDeptSql = "select dept_number,dept_name from TAB_USER";
        List<MetaData> deptList = dbSupport.find(nameDeptSql, new Object[]{}, "TAB_USER");
        for (Map<String, String> deptNum : deptList) {
            namedept.put(String.valueOf(deptNum.get("dept_number")), deptNum.get("dept_name"));
        }
        System.out.println("namedept:  " + namedept.toString());
        //-----------------------------------+
        // 设置请求参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);
        String url = "http://192.168.44.118:8080/activiti/run/queryTasks";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        // 请求activiti后台
        ResultMsg sr = HttpClientHelper.sendGet(url, "?" + sbParams, charset);

        // 接受到数据后查询数据库对应的流程信息
        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) sr.getData(), List.class);
        System.out.println("填报任务" + list);

        // 创建放入Rows的对象
        List<MetaData> strList = new ArrayList<MetaData>();
        for (Map<String, String> li : list) {
            System.out.println(li);
            String strNum = li.get("businessId");
            String taskId = li.get("taskId");
            String taskName = li.get("taskName");
            String assignee = li.get("assignee");
            String processInstanceId = li.get("processInstanceId");
            System.out.print("strNUm" + strNum);
            String sql = "select * from TAB_STR_ASSESS where STR_NUMBER=?";
            MetaData strAssess = new MetaData();
            // 查询相应审核条目数据库内容
            try {
                strAssess = dbSupport.findOne(sql, new Object[]{strNum}, "TAB_STR_ASSESS");
                System.out.println(strAssess);
                strAssess.put("taskId", taskId);
                // id设置为processInstanceId，不会出现重复id
                strAssess.put("id", processInstanceId);
                strAssess.put("str_reportstatus", taskName);
                strAssess.put("processInstanceId", processInstanceId);
                strAssess.put("str_responsibility_util", namedept.get(assignee));

                rm = new ResultMsg(0, "查询考核任务成功");
                strList.add(strAssess);
//				System.out.println("查询"+strAssess);
            } catch (Exception e) {
                rm.setMsg("查询考核任务失败：" + e.getMessage());
                e.printStackTrace();
            }

        }
        rm.setRows(strList);
        return rm;

    }

    /**
     * 优化查询考核任务（下级，上级）
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ResultMsg newQueryTasks(String id, String sidx, String sord) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        // 设置请求参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);
        String url = "http://192.168.44.118:8080/activiti/run/queryTasks";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        // 请求activiti后台
        ResultMsg sr = HttpClientHelper.sendGet(url, "?" + sbParams, charset);

        // 接受到数据后查询数据库对应的流程信息
        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) sr.getData(), List.class);
        System.out.println("填报任务" + list);

        // 创建放入Rows的对象
        List<MetaData> strList = new ArrayList<MetaData>();
        for (Map<String, String> li : list) {
            System.out.println(li);
            String strNum = li.get("businessId");
            String taskId = li.get("taskId");
            String taskName = li.get("taskName");
            String processInstanceId = li.get("processInstanceId");
            System.out.print("strNUm" + strNum);
            String sql = "select * from TAB_STR_ASSESS where STR_NUMBER=?";
            MetaData strAssess = new MetaData();
            // 查询相应审核条目数据库内容
            try {
                strAssess = dbSupport.findOne(sql, new Object[]{strNum}, "TAB_STR_ASSESS");
                System.out.println(strAssess);
                strAssess.put("taskId", taskId);
                // id设置为processInstanceId，不会出现重复id
                strAssess.put("id", processInstanceId);
                strAssess.put("str_reportstatus", taskName);
                strAssess.put("processInstanceId", processInstanceId);
                rm = new ResultMsg(0, "查询考核任务成功");
                strList.add(strAssess);
                System.out.println("查询" + strAssess);
            } catch (Exception e) {
                rm.setMsg("查询考核任务失败：" + e.getMessage());
                e.printStackTrace();
            }

        }
        rm.setRows(strList);
        return rm;

    }

    /**
     * 完成单任务（上级，下级）
     *
     * @return
     * @throws Exception
     */
    public ResultMsg completeTask(String id, String taskId, String businessId, String comment, String outcome)
            throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        // 设置请求参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);
        params.put("taskId", taskId);
        params.put("businessId", businessId);
        // 放入自己的上级id（此处固定）
        params.put("assessmentId", "xw3204110");
        params.put("comment", comment);
        params.put("outcome", outcome);
        String url = "http://192.168.44.118:8080/activiti/run/completeTask";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        // 向activiti后台发送请求
        ResultMsg sr = HttpClientHelper.sendGet(url, "?" + sbParams, charset);
        System.out.println("完成" + sr);
        // 接受到数据后查询数据库对应的流程信息
        Map<String, String> map = JSONObject.toJavaObject((JSONObject) sr.getData(), Map.class);
        // List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray)
        // sr.getData(), List.class);
        if (sr.getCode() == 1) {
            rm.setMsg(sr.getMsg());
        } else {
            rm = new ResultMsg(0, "完成单任务成功");
        }
        return rm;
    }

    /**
     * 完成多任务（上级，下级）
     *
     * @return
     * @throws Exception
     */
    public ResultMsg upTasks(String con) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        // 设置请求参数

        String url = "http://192.168.44.118:8080/activiti/run/completeTasks";
        String charset = "utf-8";

        // 向activiti后台发送请求
        ResultMsg sr = HttpClientHelper.sendGet(url, "?map=" + con, charset);
        System.out.println("完成" + sr);
        // 接受到数据后查询数据库对应的流程信息
        //Map<String, String> map = JSONObject.toJavaObject((JSONObject) sr.getData(),
        // Map.class);
        // List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray)
        // sr.getData(), List.class);
        rm = new ResultMsg(0, "完成多任务成功");
        return rm;
    }

    /**
     * 查询某人参与的流程
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ResultMsg queryProcessJoinByUserId(String id) throws Exception {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        long one = System.currentTimeMillis();
        // 设置请求参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);
        String url = "http://192.168.44.118:8080/activiti/run/queryProcessJoinByUserId";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        // 请求activiti后台
        ResultMsg sr = HttpClientHelper.sendGet(url, "?" + sbParams, charset);
//		System.out.println("填报" + sr);
        // 接受到数据后查询数据库对应的流程信息
        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) sr.getData(), List.class);
//		System.out.println("填报任务" + list);
        long two = System.currentTimeMillis();
        // 创建放入Rows的对象

        List<MetaData> strList = new ArrayList<MetaData>();
        for (Map<String, String> li : list) {
//			System.out.println(li);
            String taskId = li.get("taskId");
            String taskName = li.get("taskName");
            String strNum = li.get("businessId");
            String processInstanceId = li.get("processInstanceId");
            String sql = "select * from TAB_STR_ASSESS where STR_NUMBER=?";

            MetaData strAssess = new MetaData();

            // 查询相应审核条目数据库内容
            try {
                strAssess = dbSupport.findOne(sql, new Object[]{strNum}, "TAB_STR_ASSESS");
                if (strAssess == null) {
                    continue;
                }
//				System.out.println(strAssess);
                //若流程结束，将没有taskId，所有这里需要判断
                if (!StringUtils.isEmpty(taskId)) {
                    strAssess.put("taskId", taskId);
                    // id设置为taskId，不会出现重复id
                    strAssess.put("id", taskId);
                    strAssess.put("str_reportstatus", taskName);
                } else {
                    strAssess.put("str_reportstatus", "已审核");
                }
                strAssess.put("processInstanceId", processInstanceId);
                rm = new ResultMsg(0, "查询考核任务成功");
                strList.add(strAssess);

            } catch (Exception e) {
                rm.setMsg("查询考核任务失败：" + e.getMessage());
                e.printStackTrace();
            }

        }
        rm.setRows(strList);
        long three = System.currentTimeMillis();
        System.out.println(three - one);
        System.out.println(two - one);
        System.out.println(three - two);


        return rm;

    }

    /**
     * 优化查询某人参与的流程
     *
     * @param id
     * @return
     * @throws Exception
     */
    public ResultMsg newQueryProcessJoinByUserId(String id, String sidx, String sord) throws Exception {
        if (StringUtils.isEmpty(sidx)) {
            sidx = "str_number";
        }
        if (StringUtils.isEmpty(sord)) {
            sord = "asc";
        }
        ResultMsg rm = new ResultMsg(1, "系统异常");

        //将部门与用户存入一个Map已备查询————————+
        Map<String, String> namedept = new HashMap<String, String>();
        String nameDeptSql = "select dept_number,dept_name from TAB_USER";
        List<MetaData> deptList = dbSupport.find(nameDeptSql, new Object[]{}, "TAB_USER");
        for (Map<String, String> deptNum : deptList) {
            namedept.put(String.valueOf(deptNum.get("dept_number")), deptNum.get("dept_name"));
        }
//		System.out.println("namedept:  "+namedept.toString());
        //-----------------------------------+
        long one = System.currentTimeMillis();//activiti后台请求开始计时
        // 设置请求参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);
        String url = "http://192.168.44.118:8080/activiti/run/queryProcessJoinByUserId";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        // 请求activiti后台
        ResultMsg sr = HttpClientHelper.sendGet(url, "?" + sbParams, charset);
//		System.out.println("填报" + sr);
        // 接受到数据后查询数据库对应的流程信息
        List<Map<String, String>> list = JSONArray.toJavaObject((JSONArray) sr.getData(), List.class);
//		System.out.println("填报任务" + list);
        long two = System.currentTimeMillis();//平台请求数据库开始计时
        // 创建放入Rows的对象
        List<MetaData> strList = new ArrayList<MetaData>();
        StringBuffer strNumbers = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            String strNum = list.get(i).get("businessId");


            if (i == 0) {

                strNumbers.append(strNum);
            } else {
                strNumbers.append("," + strNum);
            }
        }
        String sql = "select * from TAB_STR_ASSESS where STR_NUMBER in(" + strNumbers + ")";


        System.out.println(sql);
        List<MetaData> strAssess = dbSupport.find(sql, new Object[]{}, "TAB_STR_ASSESS");

        System.out.println("开始");
        System.out.println(strAssess);
        //将本地指标详细内容放入流程id
        for (Map<String, String> li : list) {

            if (li == null) {
                continue;
            }
            String taskId = li.get("taskId");
            String taskName = li.get("taskName");
            String assignee = li.get("assignee");
            int flag = 0;
            String actId = li.get("businessId");
            for (Map<String, String> assList : strAssess) {
                String assId = assList.get("str_number");
                if (actId.equals(assId)) {
                    li.putAll(assList);
                    flag = 1;
                }
            }
            //若指标序号修改过，找不到对应指标，则
            if (flag == 0) {
                li.put("str_number", actId);
                li.put("str_assessmentrules", "指标已变更,");
            }
            //表中放入对应字段
            li.put("str_responsibility_util", namedept.get(assignee));
            if (!StringUtils.isEmpty(taskId)) {
                li.put("taskId", taskId);
                li.put("str_reportstatus", taskName);
            } else {
                li.put("str_reportstatus", "已审核");
            }


        }
        long three = System.currentTimeMillis();//计时结束
        //排序
        if ("str_number".equals(sidx)) {
            sortNumberMethod(list, sidx, sord);
        } else {

            sortStringMethod(list, sidx, sord);
        }
        rm.setCode(0);
        rm.setMsg("查询成功");
        rm.setRows(list);
        System.out.println(three - one + "  总请求时间");
        System.out.println(two - one + "  activiti请求数据时间");
        System.out.println(three - two + "  平台请求数据时间");
        return rm;

    }

    /**
     * 无法使用需修改
     */
    public ResultMsg processImg(String processInstanceId) throws Exception {
        ResultMsg rm = new ResultMsg(0, "系统异常");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
        String url = "http://192.168.44.118:8080/activiti/run/processImg";
        String charset = "utf-8";

        StringBuffer sbParams = new StringBuffer();
        for (Entry<String, Object> entrykey : params.entrySet()) {
            sbParams.append(entrykey.getKey());
            sbParams.append("=");
            sbParams.append(entrykey.getValue());
            sbParams.append("&");
        }
        System.out.println(sbParams);
        // 请求activiti后台
        String sr = HttpClientHelper.sendPost(url, params, charset);
        System.out.println("填报" + sr);
        return null;
    }

    /**
     * 查询街道年度指标
     */
    public ResultMsg queryStrAssess(String id) {
        ResultMsg rm = new ResultMsg(0, "系统异常");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", id);
        //判断用户权限。后续。。。
        //查询指标
        String sql = "select * from TAB_STR_ASSESS";
        List<MetaData> strAssess = dbSupport.find(sql, new Object[]{}, "TAB_STR_ASSESS");
        //如果页面单位需要换行显示而不是逗号隔开，用下面这段
//		//---------
//		for(MetaData assess:strAssess) {
//			System.out.println(assess);
//			String text=(String) assess.get("str_responsibility_util");
//			String t=text.replaceAll(",", "<br>");
//			assess.put("str_responsibility_util", t);
//		}
//		//------------
        rm.setCode(1);
        rm.setMsg("查询成功");
        rm.setRows(strAssess);
        return rm;
    }

    /**
     * 添加年度指标
     *
     * @param id
     * @return
     */
    public ResultMsg addStrAssess(HashMap map) {
        ResultMsg rm = new ResultMsg(0, "系统异常");
        Map<String, Object> params = new HashMap<String, Object>();

        //判断用户权限。后续。。。

        //查询是否有相同序号任务
        ResultMsg queryMessage = queryStrAssess("");
        List<Map<String, Object>> strAssess = (List) queryMessage.getRows();
        for (Map assess : strAssess) {
            String num = String.valueOf(assess.get("str_number"));

            if (num.equals((String) map.get("str_number"))) {
                System.out.println("序号相同");
                rm.setCode(2);
                rm.setMsg("重复");
                return rm;
            }
        }
        //添加指标
        MetaData md = MetaData.mapToMetaData(map);
        //metaData需要插入表名（id可以自动生成）
        md.put("metaName", "TAB_STR_ASSESS");
        MetaData rs = dbSupport.insertMetaData(md);
        System.out.println(rs.toString());
        if (rs != null) {
            rm.setCode(1);
            rm.setMsg("查询成功");
        }
        return rm;
    }

    /**
     * 更新年度指标
     *
     * @param map
     * @return
     */
    public ResultMsg updateStrAssess(HashMap map) {
        ResultMsg rm = new ResultMsg(0, "系统异常");
        //判断用户权限


        //更新指标
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_STR_ASSESS");
        int rs = dbSupport.update(md);
        if (rs >= 0) {
            rm.setCode(1);
            rm.setMsg("更新成功");
        }
        return rm;
    }

    /**
     * list按string排序
     *
     * @param list Map类型
     * @param sidx 排序字段名
     * @param sord 升序或者倒序
     */
    @SuppressWarnings("unchecked")
    public void sortStringMethod(List list, String sidx, String sord) {
        Collections.sort(list, new Comparator<Map<String, String>>() {

            public int compare(Map<String, String> o1,
                               Map<String, String> o2) {

                if (o1.get(sidx) == null || o2.get(sidx) == null) {
                    return o1.get("id").compareTo(o2.get("id"));
                }
                if ("asc".equals(sord)) {
                    return o1.get(sidx).compareTo(o2.get(sidx));
                }
                return o2.get(sidx).compareTo(o1.get(sidx));
            }


        });

    }

    /**
     * list按number排序
     *
     * @param list Map类型
     * @param sidx 排序字段名
     * @param sord 升序或者倒序
     */
    @SuppressWarnings("unchecked")
    public void sortNumberMethod(List<Map<String, String>> list, String sidx, String sord) {
        if ("asc".equals(sord)) {

            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = 0; j < list.size() - 1 - i; j++) {
                    int o1 = Integer.parseInt(list.get(j).get(sidx));
                    int o2 = Integer.parseInt(list.get(j + 1).get(sidx));
                    if (o1 > o2) {
                        Map temp = list.get(j);
                        list.set(j, list.get(j + 1));
                        list.set(j + 1, temp);
                    }

                }
            }
        } else {
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = 0; j < list.size() - 1 - i; j++) {
                    int o1 = Integer.parseInt(list.get(j).get(sidx));
                    int o2 = Integer.parseInt(list.get(j + 1).get(sidx));
                    if (o1 < o2) {
                        Map temp = list.get(j);
                        list.set(j, list.get(j + 1));
                        list.set(j + 1, temp);
                    }

                }
            }

        }


    }

}
