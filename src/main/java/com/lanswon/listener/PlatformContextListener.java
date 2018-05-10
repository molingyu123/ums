package com.lanswon.listener;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lanswon.generator.rich.DbSupport;
import com.lanswon.generator.rich.MetaManager;
import com.lanswon.generator.util.AppEnvirement;

public class PlatformContextListener extends ContextLoaderListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        AppEnvirement.setApplicationContext(ctx);
        AppEnvirement.setServletContext(context);
        //初始化metamanager
        DbSupport dbSupport = (DbSupport) ctx.getBean("dbSupport");
        Connection connection = null;
        try {
            connection = dbSupport.getDataSource().getConnection();
            MetaManager.initMetaManager(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}