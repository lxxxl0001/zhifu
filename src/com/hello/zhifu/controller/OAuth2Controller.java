package com.hello.zhifu.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.WeChatUtils;

@Controller
public class OAuth2Controller {

	// 组装授权url
	@RequestMapping(value = "/oauth2Url")
	public String oauth2Url(String sourceUrl, HttpServletRequest request,
			HttpServletResponse response) {
		String redirectUrl = "";
		if (sourceUrl != null) {
			String returnUrl = WeChatUtils.getDomain() + "/oauth2MeUrl?sourceUrl=" + sourceUrl;
			// 组装授权url
			redirectUrl = WeChatUtils.oAuth2Url(returnUrl);
		}
		return "redirect:" + redirectUrl;
	}

	// 获取用户信息
	@RequestMapping(value = "/oauth2MeUrl")
	public String oauth2MeUrl(String code, String sourceUrl,
			HttpServletRequest request, HttpServletResponse response) {
		// 静默获取openId
		if (code != null) {
			String openId = WeChatUtils.getOpenId(code);
			// 获取微信用户openid存储在cookie中的信息
			CookieUtils.setCookie(response, "openId", openId);
		} else {
			return "redirect:/error404";
		}
		return "redirect:" + sourceUrl;
	}
}
