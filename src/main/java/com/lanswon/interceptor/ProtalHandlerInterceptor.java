package com.lanswon.interceptor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanswon.entity.ResultMsg;
import com.lanswon.generator.rich.MetaData;
import com.lanswon.service.MenuService;
import com.lanswon.service.OptService;
import com.lanswon.service.UserService;
import com.lanswon.util.JWTUtil;

import io.jsonwebtoken.Claims;

public class ProtalHandlerInterceptor extends HandlerInterceptorAdapter {

    //private static String[] allowArray = new String[] { "admin/", "api/","phone/", "public/", "file/", "front/"};

    @Resource
    private UserService userService;
    @Resource
    private OptService optService;
    @Resource
    private MenuService menuService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        //清理浏览器缓存--(防止浏览器前进后退按钮记忆历史记录)
        response.setHeader("Cache-Control", "No-cache");
        response.setHeader("Cache-Control", "No-store");
        response.setHeader("Pragma", "No-cache");
        response.setDateHeader("Expires", -1);
        //设置cookie
        response.addHeader("Set-Cookie", "uid=112; Path=/; HttpOnly");
        //设置多个cookie
        response.addHeader("Set-Cookie", "uid=112; Path=/; HttpOnly");
        response.addHeader("Set-Cookie", "timeout=30; Path=/test; HttpOnly");
        //设置https的cookie
        response.addHeader("Set-Cookie", "uid=112; Path=/; Secure; HttpOnly");

        //验证登录
        System.out.println("开始登录验证...");
        String token = request.getHeader("Authorization");

        ObjectMapper mapper = new ObjectMapper();
        ResultMsg rm = new ResultMsg(1, "token异常");
        List<MetaData> resultOpts = new ArrayList<MetaData>();
        String currentUrl = request.getRequestURI();
        PrintWriter out = response.getWriter();
        System.out.println(currentUrl);
        if (!StringUtils.isEmpty(token)) {
            //base64进行解码
            token = new String(Base64.getDecoder().decode(token.getBytes()));
            //解析token
            Claims claims = JWTUtil.parseJWT(token);
            String jobj = claims.getSubject();
            MetaData metaData = JSONObject.parseObject(jobj, MetaData.class);
            String userId = metaData.getString("userid");
            String password = metaData.getString("password");
            rm = (ResultMsg) userService.login(userId, password);
            //用户登录查询判断
            if (rm.getCode() != 0) {
                rm.setMsg("用户名密码错误！");
                String jrm = mapper.writeValueAsString(rm);
                out.print(jrm);
                return false;
            }

            //设置统一当前请求用户id,设置后客户端不用每次请求都单独传递用户userId
            request.setAttribute("userId", userId);

            //操作权限
            rm = (ResultMsg) optService.findAllOptByUserGId(token);
            resultOpts = (List<MetaData>) rm.getRows();
            for (MetaData data : resultOpts) {
                if (currentUrl.indexOf(data.get("opturl").toString()) != -1) {
                    return true;
                }
            }
            //菜单权限
            rm = (ResultMsg) menuService.queryAllMenuByRoleId(token);
            resultOpts = (List<MetaData>) rm.getRows();
            for (MetaData data : resultOpts) {
                if (currentUrl.indexOf(data.get("opturl").toString()) != -1) {
                    return true;
                }
            }
            rm.setMsg("您的权限不足！");
//			String jrm = mapper.writeValueAsString(rm);
//			out.print(jrm);
//			return false;
        }
        rm.setMsg("您的权限不足！");
        String jrm = mapper.writeValueAsString(rm);
        out.print(jrm);
        return false;
    }

}
