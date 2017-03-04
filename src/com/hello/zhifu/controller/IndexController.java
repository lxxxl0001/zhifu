package com.hello.zhifu.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hello.zhifu.model.Award;
import com.hello.zhifu.model.Setting;
import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.service.IAwardService;
import com.hello.zhifu.service.ISettingService;
import com.hello.zhifu.service.IUserInfoService;
import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.SettingsUtil;

@Controller
public class IndexController {

	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private ISettingService settingService;
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
		Long nowTime = System.currentTimeMillis();
		Award award = awardService.current();
		map.put("period", award.getTermNum()+1);
		Long afterTime = award.getNextTime() - nowTime;
		map.put("afterTime", afterTime>0?afterTime:0);
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
		int usernum = 0;
		List<UserInfo> towlist = userInfoService.findList("parent="+user.getUserid(), null);
		for (UserInfo tow : towlist) {
			usernum+=1;
			usernum+=userInfoService.findList("parent="+tow.getUserid(), null).size();
		}
		map.put("user", user);
		map.put("usernum", usernum);
		map.put("userid", user.getUserid());
		return "geren";
	}
	
	@RequestMapping(value = "/lishi", method = RequestMethod.GET)
	public String lishi(ModelMap map, HttpServletRequest request) {
		Long nowTime = System.currentTimeMillis();
		Award award = awardService.current();
		map.put("period", award.getTermNum()+1);
		Long afterTime = award.getNextTime() - nowTime;
		map.put("afterTime", afterTime>0?afterTime:0);
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
	
	@RequestMapping(value = "/login")
	public String login(String username, String password, HttpServletRequest request) {
		if("POST".equals(request.getMethod())){
			String name = SettingsUtil.getInstance().getString("username");
			String pwd = SettingsUtil.getInstance().getString("password");
			if(username.equals(name) && password.equals(pwd)){
				request.getSession().setAttribute("user", name);
				return "redirect:/adm";
			}
		}
		return "adm/login";
	}
	
	@RequestMapping(value = "/adm", method = RequestMethod.GET)
	public String admin(ModelMap map, HttpServletRequest request) {
		Object user = request.getSession().getAttribute("user");
		if (user == null) {
			return "adm/login";
		}
		Long nowTime = System.currentTimeMillis();
		Award award = awardService.current();
		map.put("period", award.getTermNum()+1);
		Long afterTime = award.getNextTime() - nowTime;
		map.put("afterTime", afterTime>0?afterTime:0);
		Setting key9= settingService.selectByPrimaryKey(9);
		map.put("key9", key9.getMvalue());
		return "adm/setting";
	}
	
	@RequestMapping(value = "/setagent", method = RequestMethod.GET)
	public String setagent(ModelMap map, HttpServletRequest request) {
		Object user = request.getSession().getAttribute("user");
		if (user == null) {
			return "adm/login";
		}
		Setting key6= settingService.selectByPrimaryKey(6);
		map.put("key6", key6.getMvalue());
		Setting key7= settingService.selectByPrimaryKey(7);
		map.put("key7", key7.getMvalue());
		Setting key8= settingService.selectByPrimaryKey(8);
		map.put("key8", key8.getMvalue());
		return "adm/setagent";
	}
	
	@RequestMapping(value = "/setrate", method = RequestMethod.GET)
	public String setrate(ModelMap map, HttpServletRequest request) {
		Object user = request.getSession().getAttribute("user");
		if (user == null) {
			return "adm/login";
		}
		Setting key1= settingService.selectByPrimaryKey(1);
		map.put("key1", key1.getMvalue());
		Setting key2= settingService.selectByPrimaryKey(2);
		map.put("key2", key2.getMvalue());
		Setting key3= settingService.selectByPrimaryKey(3);
		map.put("key3", key3.getMvalue());
		Setting key4= settingService.selectByPrimaryKey(4);
		map.put("key4", key4.getMvalue());
		Setting key5= settingService.selectByPrimaryKey(5);
		map.put("key5", key5.getMvalue());
		Setting key10= settingService.selectByPrimaryKey(10);
		map.put("key10", key10.getMvalue());
		return "adm/setrate";
	}
	
	@ResponseBody
	@RequestMapping(value = "/MP_verify_1yfOUvVbbuMnR3UR.txt", method = RequestMethod.GET)
	public String verify(HttpServletRequest request, HttpServletResponse response) {
		return "1yfOUvVbbuMnR3UR";
	}
}
