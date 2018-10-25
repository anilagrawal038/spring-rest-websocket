package com.san.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.san.service.BootstrapService;
import com.san.util.CommonUtil;

public class AppContextLoaderListener extends ContextLoaderListener {

	BootstrapService bootstrapService;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		CommonUtil.ctx = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		bootstrapService = CommonUtil.ctx.getBean(BootstrapService.class);
		bootstrapService.startup();
		System.out.println("ServletContextListener started");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		bootstrapService.destroy();
		System.out.println("ServletContextListener destroyed");
	}
}