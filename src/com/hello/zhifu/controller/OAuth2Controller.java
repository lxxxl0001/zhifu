package com.hello.zhifu.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.service.IUserInfoService;
import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.WeChatUtils;

@Controller
public class OAuth2Controller {

	@Autowired
	private IUserInfoService userInfoService;
	
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
			Map<String, Object> map = WeChatUtils.getUserInfo(code);
			
			String openId = map.get("openid").toString();
			// 获取微信用户openid存储在cookie中的信息
			CookieUtils.setCookie(response, "openId", openId);
			
			if (StringUtils.isNotEmpty(openId)){
				UserInfo userInfo = userInfoService.selectByOpendId(openId);
				if (userInfo == null) {
					userInfo = new UserInfo();
					userInfo.setOpenid(openId);
					userInfo.setMoney(0);
					userInfo.setAgent(0);
					userInfo.setNickname(map.get("nickname")==null?"":map.get("nickname").toString());
					userInfo.setSex(map.get("sex")==null?"":map.get("sex").toString());
					userInfo.setCity(map.get("city")==null?"":map.get("city").toString());
					userInfo.setProvince(map.get("province")==null?"":map.get("province").toString());
					userInfo.setCountry(map.get("country")==null?"":map.get("country").toString());
					userInfo.setHeadimgurl(map.get("headimgurl")==null?"":map.get("headimgurl").toString());
					userInfoService.insert(userInfo);
				}else{
					userInfo.setNickname(map.get("nickname")==null?"":map.get("nickname").toString());
					userInfo.setSex(map.get("sex")==null?"":map.get("sex").toString());
					userInfo.setCity(map.get("city")==null?"":map.get("city").toString());
					userInfo.setProvince(map.get("province")==null?"":map.get("province").toString());
					userInfo.setCountry(map.get("country")==null?"":map.get("country").toString());
					userInfo.setHeadimgurl(map.get("headimgurl")==null?"":map.get("headimgurl").toString());
					userInfoService.update(userInfo);
				}
			}
		} else {
			return "redirect:/error404";
		}
		return "redirect:" + sourceUrl;
	}
}
