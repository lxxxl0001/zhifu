package com.hello.zhifu.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.WeChatUtils;

public class OAuth2Interceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		String openId = CookieUtils.getCookieValue(request, "openId");
		
		if (StringUtils.isEmpty(openId)) {
			String sourceUrl = request.getRequestURL() + "?" + request.getQueryString();
            response.sendRedirect(WeChatUtils.getDomain() + "/oauth2Url?sourceUrl=" + sourceUrl);
			return false;
		} else {
			return true;
		}
	}
}
