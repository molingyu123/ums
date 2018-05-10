package com.lanswon.service;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lanswon.entity.ResultMsg;
import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaData;
import com.lanswon.util.JWTUtil;
import com.lanswon.util.PageHelper;
import com.lanswon.util.StringUtil;
import com.lanswon.util.TokenUtil;

@Service
public class UserService {

    @Resource
    private DbSupport dbSupport;

    @Value("${checkToken}")
    private String checkToken;

    public Object login(String arg, String password) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        System.out.println("-------");
        if (!StringUtils.isEmpty(password) && password.length() != 32) {
            password = StringUtil.getEncode(password, StringUtil.MD5);
        }
        String sql = "select " + PageHelper.getColumnSql("TAB_USER") + " from TAB_USER where (USERID = ? OR EMAIL = ? OR MOBILE = ?) AND PASSWORD = ? AND STATUS = '1'";
        MetaData md = dbSupport.findOne(sql, new Object[]{arg, arg, arg, password}, "TAB_USER");

        if (md != null) {
            md.put("LAST_LOGIN_TIME", new Date());
            dbSupport.update(md);
            md.initPut("url", checkToken);
            String jobj = JSONObject.toJSONString(md);
            // 登录成功,设置token
            String token = "";
            try {
                token = JWTUtil.createJWT(arg, jobj, System.currentTimeMillis());
                // base64进行编码
                token = new String(Base64.getEncoder().encode(token.getBytes()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            MetaData metaData = new MetaData();
            metaData.initPut("userid", md.getString("userid"));
            metaData.initPut("username", md.getString("username"));
            metaData.initPut("token", token);
            rm = new ResultMsg(0, "查询成功", metaData);
        } else {
            rm.setMsg("用户名或密码不正确");
        }

        return rm;
    }

    public Object ckLogin(String token) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        if (!StringUtils.isEmpty(token)) {
            MetaData metaData = TokenUtil.getTokenMetaData(token);
            String userId = metaData.getString("userid");
            String password = metaData.getString("password");
            if (!StringUtils.isEmpty(password) && password.length() != 32) {
                password = StringUtil.getEncode(password, StringUtil.MD5);
            }
            String sql = "select " + PageHelper.getColumnSql("TAB_USER") + " from TAB_USER where USERID = ? AND PASSWORD = ? AND STATUS = '1'";
            MetaData md = dbSupport.findOne(sql, new Object[]{userId, password}, "TAB_USER");
            if (md != null) {
                MetaData mdt = new MetaData();
                mdt.initPut("userid", md.getString("userid"));
                mdt.initPut("username", md.getString("username"));
                rm = new ResultMsg(0, "查询成功", mdt);
            } else {
                rm.setMsg("用户名或密码不正确");
            }
        }
        return rm;
    }

    public Object findOneUser(String arg) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        //生成查询字段
        String nvlSql = PageHelper.getColumnSql("TAB_USER", "u");
        String sql = "select " + nvlSql + ",g.GROUPNAME from TAB_USER u left join TAB_USERGROUP g on u.USERGROUP = g.GID where (USERID = ? OR EMAIL = ? OR MOBILE = ?) AND STATUS = '1'";
        MetaData md = dbSupport.findOne(sql, new Object[]{arg, arg, arg}, "TAB_USER");
        if (md != null) {
            rm = new ResultMsg(0, "查询用户成功", md);
        } else {
            rm.setMsg("查询用户失败");
        }
        return rm;
    }

    public Object findAllUser(MetaData md) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String userName = md.getString("USERNAME");
        String mobile = md.getString("MOBILE");
        String userGroup = md.getString("USERGROUP");
        if (!PageHelper.isPositiveInteger(md)) {
            rm.setMsg("page和pageSize有误");
            return rm;
        }
        int page = md.getInt("PAGE");
        int pageSize = md.getInt("PAGESIZE");
        String nvlSql = PageHelper.getColumnSql("TAB_USER", "u");
        String sql = "select " + nvlSql + ",g.GROUPNAME from TAB_USER u left join TAB_USERGROUP g on u.USERGROUP = g.GID where 1 = 1";
        if (userName != null) {
            sql += " and USERNAME like '%" + userName + "%' ";
        }
        if (mobile != null) {
            sql += " and MOBILE like '%" + mobile + "%' ";
        }
        if (userGroup != null) {
            sql += " and USERGROUP like '%" + userGroup + "%' ";
        }
        sql += "and STATUS = '1'";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, null, "TAB_USER", page, pageSize);
        return rm;
    }

    public Object updateUserGroup(String userID, String usergroupId) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = new MetaData("TAB_USER");
        md.put("USERGROUP", usergroupId);
        md.put("USERID", userID);
        try {
            dbSupport.update(md);
            rm = new ResultMsg(0, "修改成功", md);
        } catch (Exception e) {
            rm.setMsg("修改失败:" + e.getMessage());
        }
        return rm;
    }

    public Object addUser(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_USER");
        md.put("USERID", StringUtil.getUUID().substring(0, 20));
        ResultMsg checkRm = checkValue(md, rm);
        if (checkRm != null) {
            return checkRm;
        }
        String password = md.getString("PASSWORD");
        if (StringUtils.isEmpty(password)) {
            password = "1234";
        }
        md.put("PASSWORD", StringUtil.getEncode(password, StringUtil.MD5));
        Date date = new Date();
        md.put("REGTIME", date);
        md.put("UPDATETIME", date);
        md.put("STATUS", "1");
        md.put("SEX", "1");
        md.put("DEPT_NUMBER", "1");
        md.put("RESERVEDFLAG", "1");
        try {
            MetaData returnMd = dbSupport.insertMetaData(md);
            if (returnMd != null) {
                rm = new ResultMsg(0, "添加用户成功", returnMd);
            } else {
                rm.setMsg("添加用户失败");
            }
        } catch (Exception e) {
            rm.setMsg("添加用户失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

	/*
     * public Object delUser(String id) { ResultMsg rm = new ResultMsg(1,
	 * "系统异常");
	 * 
	 * try { dbSupport.delete("TAB_USER", id); rm = new ResultMsg(0, "删除用户成功",
	 * id); } catch (Exception e) { rm.setMsg("删除用户失败:" + e.getMessage()); }
	 * return rm; }
	 */

    public Object delUser(String id) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = new MetaData();
        md.put("metaName", "TAB_USER");
        md.put("ID", id);
        md.put("STATUS", 0);
        Date date = new Date();
        md.put("UPDATETIME", date);
        try {
            dbSupport.update(md);
            rm = new ResultMsg(0, "删除用户成功", id);
        } catch (Exception e) {
            rm.setMsg("删除用户失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public Object modifyUser(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_USER");
        ResultMsg checkRm = checkValue(md, rm);
        if (checkRm != null) {
            return checkRm;
        }
        Date date = new Date();
        md.put("UPDATETIME", date);

        try {
            dbSupport.update(md);
            rm = new ResultMsg(0, "编辑用户成功", md);
        } catch (Exception e) {
            rm.setMsg("编辑用户失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public Object logOut(String id) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = new MetaData();
        md.put("metaName", "TAB_USER");
        md.put("ID", id);
        Date date = new Date();
        md.put("LAST_LOGOUT_TIME", date);
        try {
            dbSupport.update(md);
            rm = new ResultMsg(0, "登出成功", md);
        } catch (Exception e) {
            rm.setMsg("登出失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public ResultMsg checkValue(MetaData md, ResultMsg rm) {
        String userName = md.getString("USERNAME");
        String email = md.getString("EMAIL");
        System.out.println(email);
        String mobile = md.getString("MOBILE");
        String id = md.getString("ID");
        String regex = ".*[a-zA-Z]+.*";
        if (userName == null) {
            rm = new ResultMsg(1, "用户名不能为空");
            return rm;
        }
        if (email != null) {
            if (!email.contains("@") || !email.contains(".com")) {
                rm = new ResultMsg(1, "邮箱格式不正确");
                return rm;
            }
            if (hasExist(email, id)) {
                rm = new ResultMsg(1, "该邮箱已被使用");
                return rm;
            }
        } else {
            rm = new ResultMsg(1, "邮箱不能为空");
            return rm;
        }
        if (mobile != null) {
            if (mobile.matches(regex) || !(mobile.length() == 11)) {
                rm = new ResultMsg(1, "手机号码格式不正确");
                return rm;
            }
            if (hasExist(mobile, id)) {
                rm = new ResultMsg(1, "该手机号码已被使用");
                return rm;
            }
        } else {
            rm = new ResultMsg(1, "手机号码不能为空");
            return rm;
        }
        return null;
    }

    public boolean hasExist(String arg, String id) {
        boolean flag = false;
        String sql = "select * from TAB_USER where (USERID = ? OR EMAIL = ? OR MOBILE = ?) AND STATUS = '1'";
        MetaData md = dbSupport.findOne(sql, new Object[]{arg, arg, arg}, "TAB_USER");
        if (md != null) {
            if (!md.getString("id").equals(id)) {
                flag = true;
            }
        }
        return flag;
    }

    // 通过token获取最新的用户字段值
    public String getNewestVal(String token, String key) {
        MetaData metaData = TokenUtil.getTokenMetaData(token);
        if (metaData != null) {
            String id = metaData.getString("id");
            ResultMsg rm = (ResultMsg) findOneUser(id);
            if (rm.getCode() == 1) {
                return null;
            } else {
                MetaData md = (MetaData) rm.getRows();
                return md.getString(key);
            }
        }
        return null;
    }

}
