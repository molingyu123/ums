package com.lanswon.util;

import java.util.Base64;

import com.alibaba.fastjson.JSONObject;


import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaData;
import io.jsonwebtoken.Claims;

public class TokenUtil {
    /**
     * 获取token里的字段值
     *
     * @param token
     * @param key
     * @return
     */
    public static String getTokenValue(String token, String key) {
        MetaData metaData = getTokenMetaData(token);
        if (metaData != null) {
            return metaData.getString(key);
        }
        return null;
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public static MetaData getTokenMetaData(String token) {
        MetaData md = null;
        // base64进行解码
        token = new String(Base64.getDecoder().decode(token.getBytes()));
        try {
            // 解析token
            Claims claims = JWTUtil.parseJWT(token);
            String jobj = claims.getSubject();
            md = JSONObject.parseObject(jobj, MetaData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md;
    }

    /**
     * 通过token获取用户最新的字段值
     *
     * @param dbSupport
     * @param token
     * @param key
     * @return
     * @throws Exception
     */
    public static String getNewestVal(DbSupport dbSupport, String token, String key) throws Exception {
        MetaData metaData = getTokenMetaData(token);
        if (metaData != null) {
            String id = metaData.getString("id");
            String sql = "select * from TAB_USER where ID = ? AND STATUS = '1'";
            MetaData md = dbSupport.findOne(sql, new Object[]{id}, "TAB_USER");
            if (md != null) {
                return md.getString(key);
            }
        }
        return null;
    }

}
