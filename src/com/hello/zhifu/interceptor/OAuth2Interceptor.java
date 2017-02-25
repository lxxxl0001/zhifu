package com.hello.zhifu.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.service.IUserInfoService;
import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.WeChatUtils;

public class OAuth2Interceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		String openId = CookieUtils.getCookieValue(request, "openId");
		
		if (StringUtils.isEmpty(openId)) {
			String sourceUrl = request.getRequestURL().toString();
            response.sendRedirect(WeChatUtils.getDomain() + "/oauth2Url?sourceUrl=" + sourceUrl);
			return false;
		} else {
			String par = request.getParameter("pid");
			if (par != null) {
				UserInfo user = userService.selectByOpendId(openId);
				request.getSession().setAttribute("user", user);
				Integer pid = Integer.parseInt(par);
				if (user.getParent() == null && user.getUserid() != pid) {
					user.setParent(pid);
					userService.update(user);
				}
			}
			return true;
		}
	}
	
	@Autowired
	private IUserInfoService userService;
}
