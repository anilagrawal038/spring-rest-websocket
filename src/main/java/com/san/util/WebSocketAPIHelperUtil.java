package com.san.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.san.co.SocketRequestCO;
import com.san.co.SocketResponseCO;

public class WebSocketAPIHelperUtil {

	public static ApplicationContext restAppContext;
	public static RequestMappingHandlerMapping requestMappingHandlerMapping;
	public static RequestMappingHandlerAdapter requestMappingHandlerAdapter;
	private static ParameterNameDiscoverer parameterNameDiscoverer;
	private static Map<Method, String> templateURIMapping = new HashMap<Method, String>();

	public static void init() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		populateTemplateURIMapping();
		populateParameterNameDiscoverer();
	}

	public static SocketResponseCO invokeSocketAPI(SocketRequestCO request, WebSocketSession session) {
		SocketResponseCO response = new SocketResponseCO();
		response.setRequestId(request.getRequestId());
		HandlerMethod hMethod = findAPIHandlerMethod(request);
		hMethod = hMethod.createWithResolvedBean(); // hMethod.getResolvedFromHandlerMethod();
		if (hMethod != null) {
			Object bean = hMethod.getBean();
			Method method = hMethod.getMethod();
			String templateURL = fetchTemplateURI(method);
			UriTemplate uriTemplate = new UriTemplate(templateURL);
			Map<String, String> pathParams = uriTemplate.match(request.getAction());
			try {
				Object obj = null;
				try {
					MethodParameter[] methodParamTypes = hMethod.getMethodParameters();
					Object[] params = new Object[methodParamTypes.length];
					for (int counter = 0; counter < methodParamTypes.length; counter++) {
						MethodParameter methodParameter = methodParamTypes[counter];
						methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
						if (methodParameter.hasParameterAnnotation(PathVariable.class)) {
							String paramName = methodParameter.getParameterName();
							String paramValue = pathParams.get(paramName);
							Class<?> paramType = methodParameter.getParameterType();
							if (ClassUtil.isObjectOrPrimitive(paramType)) {
								paramType = ClassUtil.wrapperType(paramType);
							}
							params[methodParameter.getParameterIndex()] = paramType.getConstructor(String.class).newInstance(paramValue);
						} else if (methodParameter.hasParameterAnnotation(RequestBody.class)) {
							Class<?> paramType = methodParameter.getParameterType();
							if (ClassUtil.isObjectOrPrimitive(paramType)) {
								paramType = ClassUtil.wrapperType(paramType);
							}
							params[methodParameter.getParameterIndex()] = CommonUtil.bindJSONToObject(request.getRequestBody(), methodParameter.getParameterType());
						} else {
							// Parameter not supported
						}
					}
					obj = method.invoke(bean, params);
				} catch (Exception e) {
					response.setMessage("Unbale to process request");
				}
				response.setResponse(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				response.setMessage("Unbale to process request");
			}
		}
		return response;
	}

	private static HandlerMethod findAPIHandlerMethod(SocketRequestCO request) {
		Object handler = null;
		try {
			MockHttpServletRequest mockRequest = new MockHttpServletRequest(request.getMethod(), request.getAction());
			mockRequest.addHeader("Content-Type", "application/json");
			HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(mockRequest);
			handler = chain.getHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (HandlerMethod) handler;
	}

	private static void populateParameterNameDiscoverer() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (parameterNameDiscoverer == null) {
			Field parameterNameDiscovererField = RequestMappingHandlerAdapter.class.getDeclaredField("parameterNameDiscoverer");
			parameterNameDiscovererField.setAccessible(true);
			parameterNameDiscoverer = (ParameterNameDiscoverer) parameterNameDiscovererField.get(requestMappingHandlerAdapter);
			parameterNameDiscovererField.setAccessible(false);
		}
	}

	private static void populateTemplateURIMapping() {
		if (!templateURIMapping.isEmpty()) {
			return;
		}
		Map<RequestMappingInfo, HandlerMethod> requestMapping = requestMappingHandlerMapping.getHandlerMethods();
		Set<RequestMappingInfo> requests = requestMapping.keySet();
		for (RequestMappingInfo requestMappingInfo : requests) {
			templateURIMapping.put(requestMapping.get(requestMappingInfo).getMethod(), requestMappingInfo.getPatternsCondition().getPatterns().iterator().next());
		}
	}

	private static String fetchTemplateURI(Method method) {
		String templateURI = templateURIMapping.get(method);
		if (templateURI == null) {
			templateURI = fetchTemplateURIUsingReflection(method);
		}
		return templateURI;
	}

	private static String fetchTemplateURIUsingReflection(Method method) {
		RequestMapping requestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
		String url = null;
		String[] urls = null;
		if (requestMapping != null) {
			urls = requestMapping.value();
		}
		if (urls != null && urls.length > 0) {
			url = urls[0];
		}
		requestMapping = method.getAnnotation(RequestMapping.class);
		if (requestMapping != null) {
			urls = requestMapping.value();
		}
		if (urls != null && urls.length > 0) {
			if (url != null) {
				url += urls[0];
			} else {
				url = urls[0];
			}
		}
		return url;
	}
}
