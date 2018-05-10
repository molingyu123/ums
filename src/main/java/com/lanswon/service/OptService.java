package com.lanswon.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.lanswon.entity.ResultMsg;
import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaData;
import com.lanswon.util.TokenUtil;

@Service
public class OptService {

    @Resource
    private DbSupport dbSupport;

    @CacheEvict(value = {"findAllOptByUserGId", "findAllOpt", "updateOpt"}, allEntries = true)
//清空缓存，allEntries变量表示所有对象的缓存都清除
    public Object addOpt(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_OPT");
        try {
            ResultMsg checkRm = checkValue(md);
            if (checkRm != null) {
                return checkRm;
            }
            MetaData returnMd = dbSupport.insertMetaData(md);
            if (returnMd != null) {
                rm = new ResultMsg(0, "添加方法成功", returnMd);
            } else {
                rm.setMsg("添加方法失败");
            }
        } catch (Exception e) {
            rm.setMsg("添加方法失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    @CacheEvict(value = {"findAllOptByUserGId", "findAllOpt"}, allEntries = true)//清空缓存，allEntries变量表示所有对象的缓存都清除
    public Object updateOpt(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_OPT");
        try {
            ResultMsg checkRm = checkValue(md);
            if (checkRm != null) {
                return checkRm;
            }
            dbSupport.update(md);
            rm = new ResultMsg(0, "修改方法成功", md);
        } catch (Exception e) {
            rm.setMsg("修改方法失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    @CacheEvict(value = {"findAllOptByUserGId", "findAllOpt", "updateOpt"}, allEntries = true)
//清空缓存，allEntries变量表示所有对象的缓存都清除
    public Object delOpt(String id) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        try {
            dbSupport.delete("TAB_OPT", id);
            // 删除方法的同时需要删除方法权限
            List<String> list = getQXIdByOPTId(id);
            if (list == null) {
                rm.setMsg("移除操作权限失败");
                return rm;
            }
            for (String QXId : list) {
                dbSupport.delete("TAB_OPTQX", QXId);
            }
            rm = new ResultMsg(0, "移除操作权限成功", id);
        } catch (Exception e) {
            rm.setMsg("移除操作权限失败:" + e.getMessage());
        }
        return rm;
    }

    @Cacheable("findAllOpt") //标注该方法查询的结果进入缓存，再次访问时直接读取缓存中的数据
    public Object findAllOpt() {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> menus = new ArrayList<MetaData>();
        try {
            menus = dbSupport.find("TAB_OPT");
            rm = new ResultMsg(0, "查询成功", menus);
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        System.out.println(rm);
        System.out.println(rm.getRows());
        return rm;
    }

    public Object addOptQX(Map<String, Object> map) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        MetaData md = MetaData.mapToMetaData(map);
        md.put("metaName", "TAB_OPTQX");
        Date date = new Date();
        md.put("CREATETIME", date);
        try {
            MetaData returnMd = dbSupport.insertMetaData(md);
            if (returnMd != null) {
                rm = new ResultMsg(0, "添加权限成功", returnMd);
            } else {
                rm.setMsg("添加权限失败");
            }
        } catch (Exception e) {
            rm.setMsg("添加权限失败:" + e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public Object delOptQX(String id) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        try {
            dbSupport.delete("TAB_OPTQX", id);
            rm = new ResultMsg(0, "删除权限成功", id);
        } catch (Exception e) {
            rm.setMsg("删除权限失败:" + e.getMessage());
        }
        return rm;
    }

    public Object checkUserQX(String token, String[] optId0) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        System.out.println(optId0);
        List<String> result = new ArrayList<String>();
        try {
            String userGId = TokenUtil.getTokenValue(token, "usergroup");
            if (userGId == null) {
                rm.setMsg("用户状态异常，请重新登录");
                return rm;
            }
            for (int i = 0; i < optId0.length; i++) {
                String optId = optId0[i];
                String sql = "select * from TAB_OPTQX where USERGID = ? AND OPTID = ?";
                MetaData md = dbSupport.findOne(sql, new Object[]{userGId, optId}, "TAB_OPTQX");
                if (md != null) {
                    result.add(md.getString("optid"));
                }
            }
            rm = new ResultMsg(0, "查询成功", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rm;
    }

    @Cacheable("findAllOptByUserGId")
    // 根据用户组ID获取所有方法信息
    public Object findAllOptByUserGId(String token) {
        ResultMsg rm = new ResultMsg(1, "系统异常");
        List<MetaData> menus = new ArrayList<MetaData>();
        try {
            // String sql = "select * from TAB_OPT where id in (select OPTID
            // from TAB_OPTQX where USERGID = ?)";
            String userGId = TokenUtil.getTokenValue(token, "usergroup");
            if (userGId == null) {
                rm.setMsg("用户状态异常，请重新登录");
                return rm;
            }
            String sql = "select opt.*,nvl2(ugb.OPTID,1,0) checked,ugb.ID QXID from TAB_OPT opt left join "
                    + "(select OPTID,ID from TAB_OPTQX where USERGID = ?) ugb" + " on opt.ID = ugb.OPTID";
            menus = dbSupport.find(sql, new Object[]{userGId}, "TAB_OPT");
            rm = new ResultMsg(0, "查询成功", menus);
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public ResultMsg checkValue(MetaData md) {
        String optName = md.getString("OPTNAME");
        String optUrl = md.getString("OPTURL");
        if (StringUtils.isEmpty(optName) || StringUtils.isEmpty(optUrl)) {
            return new ResultMsg(1, "参数不能为空");
        }
        if (hasExist(optName) || hasExist(optUrl)) {
            return new ResultMsg(1, "该接口已被使用");
        }
        return null;
    }

    public boolean hasExist(String arg) {
        boolean flag = false;
        String sql = "select * from TAB_OPT where OPTNAME = ? OR OPTURL = ?";
        MetaData md = dbSupport.findOne(sql, new Object[]{arg, arg}, "TAB_OPT");
        if (md != null) {
            flag = true;
        }
        return flag;
    }

    // 根据OPTID获取OPTQX对应的ID集合
    public List<String> getQXIdByOPTId(String id) {
        String sql = "select * from TAB_OPTQX where optId = ?";
        List<String> idLists = new ArrayList<String>();
        try {
            List<MetaData> list = dbSupport.find(sql, new Object[]{id}, "TAB_OPTQX");
            for (MetaData md : list) {
                idLists.add(md.getString("id"));
            }
            return idLists;
        } catch (Exception e) {
            return null;
        }
    }

}
