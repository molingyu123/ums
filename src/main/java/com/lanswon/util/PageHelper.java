package com.lanswon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lanswon.entity.ResultMsg;
import com.lanswon.erp.model.Property;
import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaData;
import com.lanswon.generator.rich.MetaManager;

public class PageHelper {

    private static int defultPage = 1;
    private static int defultPageSize = 10;

    /**
     * 返回总数据数量
     *
     * @param dbSupport
     * @param sql
     * @param arguments
     * @param metaName
     * @return
     * @throws Exception
     */
    public static int getRecords(DbSupport dbSupport, String sql, Object[] arguments, String metaName)
            throws Exception {
        List<MetaData> menus = new ArrayList<MetaData>();
        int records = 0;
        try {
            menus = dbSupport.find(sql, arguments, metaName);
            records = menus.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 返回总页码
     *
     * @param records
     * @param pageSize
     * @return
     */
    public static int getTotal(int records, int pageSize) {
        int total = 0;
        if (!isIntegerForDouble(((double) records / pageSize))) {
            total = (records / pageSize) + 1;
        } else {
            total = (records / pageSize);
        }
        return total;
    }

    public static int getTotal(int records) {
        // pageSize默认为10
        return getTotal(records, defultPageSize);
    }

    public static int getRowNumber(int page) {
        int rownumber = getRowNumber(page, defultPageSize);
        return rownumber;
    }

    public static int getRowNumber(int page, int pageSize) {
        int rowNumber = page * pageSize;
        return rowNumber;
    }

    public static String sql(String sql, int page) {
        sql = sql(sql, page, defultPageSize);
        return sql;
    }

    // 自动生成分页sql
    public static String sql(String sql, int page, int pageSize) {
        int rowNum = getRowNumber(page, pageSize);
        String end = String.valueOf(rowNum);
        String begin = String.valueOf(rowNum - pageSize);
        sql = "select * from (" + "select ROWNUM rn,a.* from (" + sql + ")a" + ") where rn>" + begin + " and rn<="
                + end;
        return sql;
    }

    /**
     * 获取对应页码数据
     *
     * @param dbSupport
     * @param sql
     * @param arguments
     * @param metaName
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    public static List<MetaData> getRows(DbSupport dbSupport, String sql, Object[] arguments, String metaName, int page,
                                         int pageSize) throws Exception {
        sql = sql(sql, page, pageSize);
        List<MetaData> menus = null;
        menus = dbSupport.find(sql, arguments, metaName);
        return menus;
    }

    public static List<MetaData> getRows(DbSupport dbSupport, String sql, Object[] arguments, String metaName, int page)
            throws Exception {
        List<MetaData> menus = getRows(dbSupport, sql, arguments, metaName, page, defultPageSize);
        return menus;
    }

    // 查找相应页码的数据并返回ResultMsg
    public static ResultMsg getResultMsg(ResultMsg rm, DbSupport dbSupport, String sql, Object[] arguments,
                                         String metaName, int page, int pageSize) {
        int records;
        try {
            records = getRecords(dbSupport, sql, arguments, metaName);
            if (!checkPage(records, page, pageSize)) {
                rm.setMsg("page过大");
            } else {
                List<MetaData> menus = getRows(dbSupport, sql, arguments, metaName, page, pageSize);
                if (menus == null) {
                    rm.setMsg("查询失败");
                } else {
                    rm = new ResultMsg(0, "查询成功", menus);
                    rm.setRecords(records);
                    rm.setTotal(PageHelper.getTotal(records, pageSize));
                    rm.setPage(page);
                    rm.setPageSize(pageSize);
                }
            }
        } catch (Exception e) {
            rm.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return rm;
    }

    public static ResultMsg getResultMsg(ResultMsg rm, DbSupport dbSupport, String sql, Object[] arguments,
                                         String metaName, int page) {
        // pageSize默认为10
        rm = getResultMsg(rm, dbSupport, sql, arguments, metaName, page, defultPageSize);
        return rm;
    }

    // map转成MetaData,page和pageSize默认为1和10
    public static MetaData mapToMetaData(Map<String, Object> map) {
        MetaData data = new MetaData();
        data.put("PAGE", defultPage);
        data.put("PAGESIZE", defultPageSize);
        for (Object key : map.keySet()) {
            String k = (String) key;
            data.put(k.toUpperCase(), map.get(key));
        }
        return data;
    }

    /**
     * 判断double是否是整数,若为整数则为true
     *
     * @param obj
     * @return
     */
    public static boolean isIntegerForDouble(double obj) {
        double eps = 1e-10; // 精度范围
        return obj - Math.floor(obj) < eps;
    }

    /**
     * 判断page是否是有效
     */
    public static boolean checkPage(int records, int page, int pageSize) {
        boolean flag = true;
        int maxPage = getTotal(records, pageSize);
        // 最大页数不能小于页码，数据量为0情况除外
        if (maxPage < page && records != 0) {
            flag = false;
        }
        return flag;
    }


    /**
     * 判断传入的page和pageSize是否是正整数
     */
    public static boolean isPositiveInteger(MetaData md) {
        boolean flag = true;
        if (!(isIntegerForDouble(md.getDouble("PAGE")) && isIntegerForDouble(md.getDouble("PAGESIZE")))) {
            flag = false;
        }
        if (md.getDouble("PAGE") <= 0 || md.getDouble("PAGESIZE") <= 0) {
            flag = false;
        }
        return flag;
    }


    /**
     * 生成字段语句，替代select *,支持表别名
     *
     * @param metaName
     * @param nickName
     * @return
     */
    public static String getColumnSql(String metaName, String nickName) {
        List<Property> list = MetaManager.getMetaManager().getEntity(metaName).getProperty();
        String sql = "";
        for (int index = 0; index < list.size(); index++) {
            Property property = list.get(index);
            String s = property.getColumnName().toUpperCase();
            if (index == list.size() - 1) {
                sql += nickName + "." + s + " " + s;
            } else {
                sql += nickName + "." + s + " " + s + ",";
            }
        }
        return sql;
    }

    /**
     * 生成字段语句，替代select *
     *
     * @param metaName
     * @param nickName
     * @return
     */
    public static String getColumnSql(String metaName) {
        List<Property> list = MetaManager.getMetaManager().getEntity(metaName).getProperty();
        String sql = "";
        for (int index = 0; index < list.size(); index++) {
            Property property = list.get(index);
            String s = property.getColumnName().toUpperCase();
            if (index == list.size() - 1) {
                sql += s;
            } else {
                sql += s + ",";
            }
        }
        return sql;
    }

}
