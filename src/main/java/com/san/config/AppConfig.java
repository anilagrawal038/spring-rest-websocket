package com.san.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = { "com.san.service" })
@Import({ JPAConfig.class })
public class AppConfig {

}
