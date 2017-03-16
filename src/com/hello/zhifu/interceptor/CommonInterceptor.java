package com.hello.zhifu.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hello.zhifu.utils.SettingsUtil;

public class CommonInterceptor extends HandlerInterceptorAdapter {

	/**
	 * 部署路径属性名称
	 */
	public static final String CONTEXT_PATH = "base";

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object arg2) throws Exception {
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null ) {
			modelAndView.addObject(CONTEXT_PATH, request.getContextPath());
			String domain = SettingsUtil.getInstance().getString("domain");
			modelAndView.addObject("domain", domain);
			String rukou = SettingsUtil.getInstance().getString("rukou");
			modelAndView.addObject("rukou", rukou);
		}
	}
}
