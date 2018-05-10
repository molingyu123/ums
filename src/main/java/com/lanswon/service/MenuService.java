package com.lanswon.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lanswon.entity.ResultMsg;
import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaData;
import com.lanswon.util.PageHelper;
import com.lanswon.util.TokenUtil;

@Service
public class MenuService {

    @Resource
    private DbSupport dbSupport;

    // 查询所有的菜单--供选择的
    public Object queryAllMenus(MetaData md) {
        int page = md.getInt("PAGE");
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU ORDER BY NODEID";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, null, "TAB_MENU", page);
        return rm;
    }
/**/
    public String findOneINMenu(String id) {
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU where id = ?";
        try {
            MetaData md = dbSupport.findOne(sql, new Object[]{id}, "TAB_MENU");
            String nodeId = md.getString("NODEID");
            return nodeId;
        } catch (Exception e) {
            return null;
        }
    }

    // 根据MENUID获取MENUQX对应的ID集合
    public List<String> getQXIdByMenuId(String id) {
        String menuId = findOneINMenu(id);
        String sql = "select * from TAB_MENUQX where meneId = ?";
        List<String> idLists = new ArrayList<String>();
        try {
            List<MetaData> list = dbSupport.find(sql, new Object[]{menuId}, "TAB_MENUQX");
            for (MetaData md : list) {
                idLists.add(md.getString("ID"));
            }
            return idLists;
        } catch (Exception e) {
            return null;
        }
    }

    public Object queryFirstLevelMenu(MetaData md) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        int page = md.getInt("PAGE");
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU where NODEPID is null";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, null, "TAB_MENU", page);
        return rm;
    }

    // 根据nodeId 查询该节点下一级子节点
    public Object querySubMenuByNodeId(MetaData md) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String nodeId = md.getString("NODEID");
        int page = md.getInt("PAGE");
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU where NODEPID =? ORDER BY NODEID";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, new Object[]{nodeId}, "TAB_MENU", page);
        return rm;
    }

    // 根据角色查询1级菜单
    public Object queryFirstLevelMenuByRoleId(String usergroupId, int page, int pageSize) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        // 查询当前角色的menu
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU where NODEPID is null and NODEID in (select MENUID from TAB_MENUQX where USERGROUPID = ?) ORDER BY NODEID";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, new Object[]{usergroupId}, "TAB_MENU", page, pageSize);
        return rm;
    }

    // 根据用户id查询1级菜单
    public Object queryFirstLevelMenuByUserId(String userId) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> menus = new ArrayList<MetaData>();
        // 查询当前角色的menu
        try {
            String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                    + " from TAB_MENU where NODEPID is null and NODEID in ("
                    + "select MENUID from TAB_MENUQX where USERGROUPID = (select USERGROUP from TAB_USER where USERID=?)) ORDER BY NODEID";
            menus = dbSupport.find(sql, new Object[]{userId}, "TAB_MENU");
            rm = new ResultMsg(0, "查询成功", menus);
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    // 根据用户id查询2级菜单
    public Object queryAllSubMenusByUserId(String userId) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> menus = new ArrayList<MetaData>();
        // 查询当前角色的menu
        try {
            String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                    + " from TAB_MENU where NODEPID is not null and NODEID in ("
                    + "select MENUID from TAB_MENUQX where USERGROUPID = (select USERGROUP from TAB_USER where USERID=?)) ORDER BY NODEID";
            menus = dbSupport.find(sql, new Object[]{userId}, "TAB_USER");
            /*
			 * for(MetaData md : menus){ String pid = md.getString("nodeid");
			 * String sql1 = "select * from TAB_MENU where NODEPID = ?";
			 * subMenus.addAll(dbSupport.find(sql1, new Object[] { pid },
			 * "TAB_MENU")); }
			 */
            rm = new ResultMsg(0, "查询成功", menus);
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    /**
     * 查询用户组菜单<br>
     * 根据用户组查询所有菜单与其所拥有的菜单
     *
     * @param usergroupId
     * @return ResultMsg
     * @author zh
     */
    public Object queryAllMenuByRoleId(String token) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> menus = new ArrayList<MetaData>();
        try {
            String usergroupId = TokenUtil.getTokenValue(token, "usergroup");
            if (usergroupId == null) {
                rm.setMsg("用户状态异常，请重新登录");
                return rm;
            }
            String sql = "select menu.*,nvl2(mqb.MENUID,1,0) checked,mqb.ID QXID from TAB_MENU menu left join "
                    + "(select MENUID,ID from TAB_MENUQX where USERGROUPID = ?) mqb"
                    + " on menu.NODEID = mqb.MENUID ORDER BY NODEID";
            menus = dbSupport.find(sql, new Object[]{usergroupId}, "TAB_MENU");
            rm = new ResultMsg(0, "查询成功", menus);
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public Object queryMenus(MetaData md) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String nodePId = md.getString("NODEPID");
        String token = md.getString("TOKEN");
        int page = md.getInt("PAGE");
        int pageSize = md.getInt("PAGESIZE");
        try {
            if (token == null) {
                rm.setMsg("usergroupId不能为空");
                return rm;
            }
            // 根据token获取最新状态
            String usergroupId = TokenUtil.getNewestVal(dbSupport, token, "usergroup");
            if (usergroupId == null) {
                rm.setMsg("用户组异常，请重新登录");
            } else {
                if (nodePId == null) {
                    return (ResultMsg) queryFirstLevelMenuByRoleId(usergroupId, page, pageSize);
                }
                if (nodePId != null) {
                    return (ResultMsg) querySubMenuByGId(nodePId, usergroupId, page, pageSize);
                }
            }
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    // 根据角色查询该节点下一级子节点
    public Object querySubMenuByGId(String nodeId, String usergroupId, int page, int pageSize) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU where NODEPID =? and NODEID in (select MENUID from TAB_MENUQX where USERGROUPID = ?) ORDER BY NODEID";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, new Object[]{nodeId, usergroupId}, "TAB_MENU", page, pageSize);
        return rm;
    }

    public Object querySubMenuByUserId(String nodeId, String userId, int page) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU where NODEPID =? and NODEID in "
                + "(select MENUID from TAB_MENUQX where USERGROUPID = (select USERGROUP from TAB_USER where USERID=?)) ORDER BY NODEID";
        rm = PageHelper.getResultMsg(rm, dbSupport, sql, new Object[]{nodeId, userId}, "TAB_MENU", page);
        return rm;
    }

    public Object addMenuQX(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_MENUQX");
        String menuId = md.getString("MENUID");
        try {
            String userGroupId = md.getString("USERGROUPID");
            if (checkQXhasExist(menuId, userGroupId) == null) {
                String parentId = findNodePidByMenuId(menuId);
                //结束递归的条件
                if (parentId == null) {
                    return new ResultMsg(0, "添加权限成功");
                }
                md.put("PARENTID", parentId);
                MetaData returnMd = dbSupport.insertMetaData(md);
                if (returnMd != null) {
                    if (checkQXhasExist(parentId, userGroupId) == null) {
                        Map<String, Object> mapP = new HashMap<String, Object>();
                        mapP.put("USERGROUPID", userGroupId);
                        mapP.put("MENUID", parentId);
                        rm = (ResultMsg) addMenuQX(mapP);
                    }
                } else {
                    rm.setMsg("添加权限失败");
                }
            } else {
                rm.setMsg("该权限已存在");
            }
        } catch (Exception e) {
            rm.setMsg("添加权限失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    // 通过QX表的MENUID获取父类方法NODEID
    public String findNodePidByMenuId(String menuId) {
        String sql = "select ID,NODEID,NAME,SHOWNAME,CAPTION,NODEICON,POSITION,NODEPID,HASCHILDREN,nvl(NODEURL,'null') NODEURL,ISNEW,CREATETIME,RESERVED_FLAG,ADDFLAG,EDITFLAG,DELFLAG,LEAF,EXPANDED"
                + " from TAB_MENU where NODEID =?";
        MetaData menu = dbSupport.findOne(sql, new Object[]{menuId}, "TAB_MENU");
        String nodepid = null;
        if (menu != null) {
            nodepid = menu.getString("nodepid");
        }
        return nodepid;
    }

    // 判断用户组与菜单的权限是否已存在
    public MetaData checkQXhasExist(String menuId, String userGroupId) throws Exception {
        String sql = "select * from TAB_MENUQX where MENUID =? and USERGROUPID =?";
        MetaData meta = dbSupport.findOne(sql, new Object[]{menuId, userGroupId}, "TAB_MENUQX");
        return meta;
    }

    public Object delMenuQX(String menuId, String userGroupId) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        try {
            MetaData md = checkQXhasExist(menuId, userGroupId);
            if (md == null) {
                rm.setMsg("权限不存在");
                return rm;
            }
            String id = md.getString("id");
            String sql1 = "select * from TAB_MENUQX where PARENTID =? and USERGROUPID =?";
            List<MetaData> meta1 = dbSupport.find(sql1, new Object[]{menuId, userGroupId}, "TAB_MENUQX");
            if (meta1.size() > 0) {
                rm.setMsg("必须先取消下级菜单权限");
                return rm;
            }
            dbSupport.delete("TAB_MENUQX", id);
            rm = new ResultMsg(0, "删除菜单权限成功", id);
        } catch (Exception e) {
            rm.setMsg("删除菜单权限失败:" + e.getMessage());
        }
        return rm;
    }

    // 动态生成menuId
    public String createMenuId(MetaData meta) {
        String IDBase = meta.getString("nodeid");
        String PId = meta.getString("nodepid");
        int index = 0;
        // 生成一级菜单nodeid
        if ("0".equals(PId)) {
            index = 0;
        } else {
            String second = PId.substring(2, 4);
            if ("00".equals(second)) {
                // 生成2级菜单
                index = 2;
            } else if (!"00".equals(second)) {
                // 生成3级菜单
                index = 4;
            }
        }
        int sum = Integer.parseInt(IDBase.substring(0, index + 2)) + 1;
        String zero = "";
        for (int i = 0; i < (4 - index); i++) {
            zero += "0";
        }
        String IDCreate = sum + zero;
        return IDCreate;
    }

    public Object addMenu(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        ResultMsg checkRm1 = checkValue(md, rm);
        if (checkRm1 != null) {
            return checkRm1;
        }
        String nodeId = md.getString("NODEID");
        String pId = md.getString("PID");
        // 生成一级菜单
        if (nodeId == null && pId == null) {
            String sqll = "select * from (select * from TAB_MENU order by NODEID desc) WHERE ROWNUM <= 1";
            MetaData meta = dbSupport.findOne(sqll, null, "TAB_MENU");
            meta.put("nodepid", "0");
            System.out.println(meta);
            String IDCreate = createMenuId(meta);
            md.put("NODEPID", 0);
            md.put("NODEID", IDCreate);
        }
        // 生成下级菜单
        if (nodeId == null && pId != null) {
            MetaData mdP = new MetaData("TAB_MENU");
            String sql = "select * from TAB_MENU where id = ?";
            mdP = dbSupport.findOne(sql, new Object[]{pId}, "TAB_MENU");
            if (mdP != null) {
                String nodePId = mdP.getString("nodeid");
                mdP.put("HASCHILDREN", 1);
                mdP.put("ID", pId);
                dbSupport.update(mdP);
                md.put("NODEPID", nodePId);
                String sqll = "select * from (select * from TAB_MENU WHERE NODEPID = " + nodePId
                        + " order by NODEID desc) where ROWNUM <= 1";
                MetaData meta = dbSupport.findOne(sqll, null, "TAB_MENU");
                if (meta == null) {
                    meta = new MetaData();
                    meta.put("nodepid", nodePId);
                    meta.put("nodeid", nodePId);
                    String IDCreate = createMenuId(meta);
                    md.put("NODEID", IDCreate);
                } else {
                    String IDCreate = createMenuId(meta);
                    md.put("NODEID", IDCreate);
                }
            } else {
                rm.setMsg("上级菜单不存在");
                return rm;
            }
        }
        md.put("metaName", "TAB_MENU");
        Date date = new Date();
        md.put("CREATETIME", date);
        md.put("POSITION", 80);
        md.put("ISNEW", 0);
        md.put("RESERVER_FLAG", 1);
        md.put("ADDFLAG", 1);
        md.put("EDITFLAG", 1);
        md.put("DELFLAG", 1);
        md.put("HASCHILDREN", 0);
        try {
            MetaData returnMd = dbSupport.insertMetaData(md);
            if (returnMd != null) {
                rm = new ResultMsg(0, "添加菜单成功", returnMd);
            } else {
                rm.setMsg("添加菜单失败");
            }
        } catch (Exception e) {
            rm.setMsg("添加菜单失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public Object updateMenu(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_MENU");
        ResultMsg checkRm = checkValue(md, rm);
        if (checkRm != null) {
            return checkRm;
        }
        try {
            dbSupport.update(md);
			/*
			 * //修改菜单表的同时需要修改权限表 ResultMsg resultQX = updateQX(md); if(resultQX
			 * == null){ rm = new ResultMsg(0, "编辑菜单成功", md); }else{ return
			 * resultQX; }
			 */
            rm = new ResultMsg(0, "编辑菜单成功", md);
        } catch (Exception e) {
            rm.setMsg("编辑菜单失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }


    public Object delMenu(String id) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        try {
            delTotalSubMenus(id);
            rm = new ResultMsg(0, "删除菜单成功", id);
        } catch (Exception e) {
            rm.setMsg("删除菜单失败:" + e.getMessage());
        }
        return rm;
    }

    public void delTotalSubMenus(String id) {
        MetaData menu = new MetaData();
        String sql = "select * from TAB_MENU where ID =?";
        menu = dbSupport.findOne(sql, new Object[]{id}, "TAB_MENU");
        if (menu != null) {
            String nodeId = menu.getString("nodeid");
            String pid = menu.getString("nodepid");
            List<MetaData> menus = new ArrayList<MetaData>();
            String sql1 = "select * from TAB_MENU where NODEPID =?";
            menus = dbSupport.find(sql1, new Object[]{nodeId}, "TAB_MENU");
            dbSupport.delete("TAB_MENU", id);
            // 删除菜单表的同时需要删除权限表
            List<String> list = getQXIdByMenuId(id);
            if (list != null) {
                for (String menuID : list) {
                    dbSupport.delete("TAB_USERQX", menuID);
                }
            }
            for (MetaData md : menus) {
                String idSub = md.getString("id");
                delTotalSubMenus(idSub);
            }
            if (pid != null) {
                String sql2 = "select * from TAB_MENU where NODEPID =?";
                menus = dbSupport.find(sql2, new Object[]{pid}, "TAB_MENU");
                if (menus.size() == 0) {
                    String sq3 = "select * from TAB_MENU where NODEID =?";
                    menu = dbSupport.findOne(sq3, new Object[]{pid}, "TAB_MENU");
                    String Pid = menu.getString("id");
                    MetaData data = new MetaData("TAB_MENU");
                    data.put("ID", Pid);
                    data.put("HASCHILDREN", 0);
                    dbSupport.update(data);
                }
            }
        }
    }

    public ResultMsg checkValue(MetaData md, ResultMsg rm) {
        String nodeId = md.getString("NODEID");
        String name = md.getString("NAME");
        String showName = md.getString("SHOWNAME");
        String id = md.getString("ID");
        if (hasExist(nodeId, id)) {
            rm = new ResultMsg(1, "该nodeId已被使用");
            return rm;
        }
        if (name != null) {
            if (hasExist(name, id)) {
                rm = new ResultMsg(1, "该name已被使用");
                return rm;
            }
        } else {
            rm = new ResultMsg(1, "name不能为空");
            return rm;
        }
        if (showName == null) {
            rm = new ResultMsg(1, "showName不能为空");
            return rm;
        }
        return null;
    }

    public boolean hasExist(String arg, String id) {
        boolean flag = false;
        String sql = "select * from TAB_MENU where NODEID = ? OR NAME = ? ";
        MetaData md = dbSupport.findOne(sql, new Object[]{arg, arg}, "TAB_MENU");
        if (md != null) {
            if (!md.getString("id").equals(id)) {
                flag = true;
            }
        }
        return flag;
    }

}
