package com.lanswon.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lanswon.entity.ResultMsg;
import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaData;
import com.lanswon.util.PageHelper;
import com.lanswon.util.StringUtil;

@Service
public class UserGroupService {

    @Resource
    private DbSupport dbSupport;

    public Object updateUserGroup(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_USERGROUP");
        ResultMsg checkRm = checkValue(md, rm);
        if (checkRm != null) {
            return checkRm;
        }
        try {
            dbSupport.update(md);
            rm = new ResultMsg(0, "修改用户群成功", md);
        } catch (Exception e) {
            rm.setMsg("修改用户群失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public Object addUserGroup(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_USERGROUP");
        md.put("ID", StringUtil.getUUID());
        ResultMsg checkRm = checkValue(md, rm);
        if (checkRm != null) {
            return checkRm;
        }
        Date date = new Date();
        md.put("CREATETIME", date);
        try {
            MetaData returnMd = dbSupport.insertMetaData(md);
            if (returnMd != null) {
                rm = new ResultMsg(0, "添加用户组成功", returnMd);
            } else {
                rm.setMsg("添加用户组失败");
            }
        } catch (Exception e) {
            rm.setMsg("添加用户组失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public Object delUserGroup(String id, String userGroup) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        try {
            dbSupport.delete("TAB_USERGROUP", id);
            // 需要同时修改用户表
            List<String> userList = getUserIdByUsergroupId(userGroup);
            if (userList == null) {
                rm.setMsg("删除用户组失败");
                return rm;
            }
            for (String userId : userList) {
                MetaData md = new MetaData("TAB_USER");
                md.put("ID", userId);
                md.put("USERGROUP", null);
                dbSupport.update(md);
            }
            // 需要同时修改菜单权限表
            ResultMsg menuRM = delMenuQXByUsergroupId(userGroup);
            if (menuRM != null) {
                return menuRM;
            }
            //// 需要同时修改方法权限表
            ResultMsg optRM = delOPTQXByUsergroupId(userGroup);
            if (optRM != null) {
                return optRM;
            }
            rm = new ResultMsg(0, "删除用户组成功");
        } catch (Exception e) {
            rm.setMsg("删除用户组失败:" + e.getMessage());
        }
        return rm;
    }

    public Object findAllUserGroup(MetaData md) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        if (!PageHelper.isPositiveInteger(md)) {
            rm.setMsg("page和pageSize有误");
            return rm;
        }
        int page = md.getInt("PAGE");
        int pageSize = md.getInt("PAGESIZE");
        String sql = "select " + PageHelper.getColumnSql("TAB_USERGROUP") + " from TAB_USERGROUP";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, null, "TAB_USERGROUP", page, pageSize);
        return rm;
    }

    public Object findGroupUsers(String id) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> menus = new ArrayList<MetaData>();
        // 查询当前角色的menu
        try {
            String sql = "select * from TAB_USER where USERGROUP = (select GID from TAB_USERGROUP where ID = ?) and STATUS=1";
            menus = dbSupport.find(sql, new Object[]{id}, "TAB_USER");
            rm = new ResultMsg(0, "查询成功", menus);
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public ResultMsg checkValue(MetaData md, ResultMsg rm) {
        String groupName = md.getString("GROUPNAME");
        String id = md.getString("ID");
        if (groupName != null) {
            if (hasExist(groupName, id)) {
                rm = new ResultMsg(1, "该用户组名已被使用");
                return rm;
            }
        } else {
            rm = new ResultMsg(1, "用户组名不能为空");
            return rm;
        }
        return null;
    }

    public boolean hasExist(String arg, String id) {
        boolean flag = false;
        String sql = "select * from TAB_USERGROUP where GROUPNAME = ? OR GID = ?";
        MetaData md = dbSupport.findOne(sql, new Object[]{arg, arg}, "TAB_USERGROUP");
        if (md != null) {
            if (!md.getString("id").equals(id)) {
                flag = true;
            }
        }
        return flag;
    }

    // 根据USERGROUPID获取USER对应的ID集合
    public List<String> getUserIdByUsergroupId(String userGroup) {
        String sql = "select * from TAB_USER where userGroup = ?";
        List<String> idLists = new ArrayList<String>();
        try {
            List<MetaData> list = dbSupport.find(sql, new Object[]{userGroup}, "TAB_USER");
            for (MetaData md : list) {
                idLists.add(md.getString("id"));
            }
            return idLists;
        } catch (Exception e) {
            return null;
        }
    }

    // 根据USERGROUPID获取MENUQX对应的ID集合
    public ResultMsg delMenuQXByUsergroupId(String userGroupId) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String sql = "select * from TAB_MENUQX where USERGROUPID = ?";
        try {
            List<MetaData> list = dbSupport.find(sql, new Object[]{userGroupId}, "TAB_MENUQX");
            for (MetaData md : list) {
                dbSupport.delete("TAB_MENUQX", md.getString("id"));
            }
            return null;
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            return rm;
        }
    }

    // 根据USERGROUPID获取OPTQX对应的ID集合
    public ResultMsg delOPTQXByUsergroupId(String userGroupId) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String sql = "select * from TAB_OPTQX where userGId = ?";
        try {
            List<MetaData> list = dbSupport.find(sql, new Object[]{userGroupId}, "TAB_OPTQX");
            for (MetaData md : list) {
                dbSupport.delete("TAB_OPTQX", md.getString("id"));
            }
            return null;
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            return rm;
        }
    }

}
