//-----------------------------------------------------------------------------------------------------------
//					ORGANIZATION NAME
//Group							: Common - Project
//Product / Project				: spring-jpa-rest-swagger
//Module						: spring-jpa-rest-swagger
//Package Name					: com.san.config
//File Name						: RestConfig.java
//Author						: anil
//Contact						: anilagrawal038@gmail.com
//Date written (DD MMM YYYY)	: 11-Mar-2017 7:07:27 PM
//Description					:  
//-----------------------------------------------------------------------------------------------------------
//					CHANGE HISTORY
//-----------------------------------------------------------------------------------------------------------
//Date			Change By		Modified Function 			Change Description (Bug No. (If Any))
//(DD MMM YYYY)
//11-Mar-2017   	anil			N/A							File Created
//-----------------------------------------------------------------------------------------------------------

package com.san.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.san.util.WebSocketAPIHelperUtil;

@Configuration
@ComponentScan(basePackages = { "com.san.rest" })
@Import({ SwaggerConfig.class })
@EnableWebMvc
public class RestConfig extends WebMvcConfigurationSupport {

	// Required for swagger integration
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	// Set a handler to manage above resources
	@Override
	@Bean
	public HandlerMapping resourceHandlerMapping() {
		AbstractHandlerMapping handlerMapping = (AbstractHandlerMapping) super.resourceHandlerMapping();
		handlerMapping.setOrder(-1);
		return handlerMapping;
	}

	@Bean
	@Primary
	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		// Must be @Primary for MvcUriComponentsBuilder to work
		RequestMappingHandlerMapping requestMappingHandlerMapping = super.requestMappingHandlerMapping();
		WebSocketAPIHelperUtil.requestMappingHandlerMapping = requestMappingHandlerMapping;
		return requestMappingHandlerMapping;
	}

	@Bean
	@Override
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter requestMappingHandlerAdapter = super.requestMappingHandlerAdapter();
		WebSocketAPIHelperUtil.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
		return requestMappingHandlerAdapter;
	}

	// After Complete Initialization
	@EventListener({ ContextRefreshedEvent.class })
	public void started() {
		WebSocketAPIHelperUtil.restAppContext = getApplicationContext();
		try {
			WebSocketAPIHelperUtil.init();
		} catch (Exception e) {
			System.out.println("`````````````````````````````````` WebSocketAPI may not work ``````````````````````````````````");
			e.printStackTrace();
		}
	}

}
