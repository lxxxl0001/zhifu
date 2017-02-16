package com.hello.zhifu.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hello.zhifu.utils.NoException;

public class LoginInterceptor extends HandlerInterceptorAdapter {

	public String[] allowUrls;

	public void setAllowUrls(String[] allowUrls) {
		this.allowUrls = allowUrls;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object arg2) throws Exception {

		String base = request.getContextPath();
		String requestUrl = request.getRequestURI();
		if(requestUrl.endsWith(base+"/")){
			return true;
		}
		if (null != allowUrls && allowUrls.length >= 1) {
			for (String urls : allowUrls) {
				if (requestUrl.contains(urls)) {
					return true;
				}
			}
		}
		Object user = request.getSession().getAttribute("user");
		if (user == null) {
			throw new NoException();
		}
		return true;
	}
}
