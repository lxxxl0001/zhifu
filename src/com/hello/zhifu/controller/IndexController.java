package com.hello.zhifu.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.service.IAwardService;
import com.hello.zhifu.service.IUserInfoService;
import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.WeChatUtils;

@Controller
public class IndexController {

	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private IAwardService awardService;
	
	@RequestMapping(value = "/touzhu", method = RequestMethod.GET)
	public String touzhu(Integer uid, ModelMap map, HttpServletRequest request) {
		String openId = CookieUtils.getCookieValue(request, "openId");
		UserInfo user = userInfoService.selectByOpendId(openId);
		map.put("userid", user.getUserid());
		return "touzhu";
	}
	
	@RequestMapping(value = "/erweima", method = RequestMethod.GET)
	public String erweima(ModelMap map, HttpServletRequest request) {
		String openId = CookieUtils.getCookieValue(request, "openId");
		UserInfo user = userInfoService.selectByOpendId(openId);
		map.put("user", user);
		return "erweima";
	}
	
	@RequestMapping(value = "/geren", method = RequestMethod.GET)
	public String geren(ModelMap map, HttpServletRequest request) {
		String openId = CookieUtils.getCookieValue(request, "openId");
		UserInfo user = userInfoService.selectByOpendId(openId);
		map.put("user", user);
		return "geren";
	}
	
	@RequestMapping(value = "/lishi", method = RequestMethod.GET)
	public String lishi(HttpServletRequest request, HttpServletResponse response) {
		return "lishi";
	}
	
	@RequestMapping(value = "/zhibo", method = RequestMethod.GET)
	public String zhibo(HttpServletRequest request, HttpServletResponse response) {
		return "zhibo";
	}
	
	@RequestMapping(value = "/error404", method = RequestMethod.GET)
	public String error404(HttpServletRequest request, HttpServletResponse response) {
		return "error";
	}
	
	@ResponseBody
	@RequestMapping(value = "/MP_verify_1yfOUvVbbuMnR3UR.txt", method = RequestMethod.GET)
	public String verify(HttpServletRequest request, HttpServletResponse response) {
		return "1yfOUvVbbuMnR3UR";
	}
}
