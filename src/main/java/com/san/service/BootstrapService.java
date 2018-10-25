package com.san.service;

import org.springframework.stereotype.Component;

@Component
public class BootstrapService {

	public void startup() {
		System.out.println("inside startup");
	}

	public void destroy() {
		System.out.println("inside destroy");
	}

}
