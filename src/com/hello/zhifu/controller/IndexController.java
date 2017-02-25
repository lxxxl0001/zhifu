package com.hello.zhifu.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.service.IAwardService;
import com.hello.zhifu.service.IUserInfoService;
import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.SettingsUtil;

@Controller
public class IndexController {

	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private IAwardService awardService;
	
	@RequestMapping(value = "/touzhu", method = RequestMethod.GET)
	public String touzhu(Integer pid, ModelMap map, HttpServletRequest request) {
		String openId = CookieUtils.getCookieValue(request, "openId");
		UserInfo user = userInfoService.selectByOpendId(openId);
		//更新上级id上级id不能是自己
		if (user.getParent() == null && user.getUserid() != pid) {
			user.setParent(pid);
			userInfoService.update(user);
		}
		map.put("userid", user.getUserid());
		return "touzhu";
	}
	
	@RequestMapping(value = "/erweima", method = RequestMethod.GET)
	public String erweima(Integer pid, ModelMap map, HttpServletRequest request) {
		String openId = CookieUtils.getCookieValue(request, "openId");
		UserInfo user = userInfoService.selectByOpendId(openId);
		//更新上级id上级id不能是自己
		if (user.getParent() == null && user.getUserid() != pid) {
			user.setParent(pid);
			userInfoService.update(user);
		}
		map.put("userid", user.getUserid());
		String domain = SettingsUtil.getInstance().getString("domain");
		map.put("domain", domain);
		return "erweima";
	}
	
	@RequestMapping(value = "/geren", method = RequestMethod.GET)
	public String geren(Integer pid, ModelMap map, HttpServletRequest request) {
		String openId = CookieUtils.getCookieValue(request, "openId");
		UserInfo user = userInfoService.selectByOpendId(openId);
		//更新上级id上级id不能是自己
		if (user.getParent() == null && user.getUserid() != pid) {
			user.setParent(pid);
			userInfoService.update(user);
		}
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
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response) {
		return "adm/login";
	}
	
	@RequestMapping(value = "/adm", method = RequestMethod.GET)
	public String admin(HttpServletRequest request, HttpServletResponse response) {
		Object user = request.getSession().getAttribute("user");
		if (user == null) {
			//return "adm/login";
		}
		return "adm/setting";
	}
	
	@RequestMapping(value = "/setagent", method = RequestMethod.GET)
	public String setagent(HttpServletRequest request, HttpServletResponse response) {
		Object user = request.getSession().getAttribute("user");
		if (user == null) {
			//return "adm/login";
		}
		return "adm/setagent";
	}
	
	@RequestMapping(value = "/setrate", method = RequestMethod.GET)
	public String setrate(HttpServletRequest request, HttpServletResponse response) {
		Object user = request.getSession().getAttribute("user");
		if (user == null) {
			//return "adm/login";
		}
		return "adm/setrate";
	}
	
	@ResponseBody
	@RequestMapping(value = "/MP_verify_1yfOUvVbbuMnR3UR.txt", method = RequestMethod.GET)
	public String verify(HttpServletRequest request, HttpServletResponse response) {
		return "1yfOUvVbbuMnR3UR";
	}
}
