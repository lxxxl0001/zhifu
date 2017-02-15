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
		return "error404";
	}
	
	@ResponseBody
	@RequestMapping(value = "/MP_verify_1yfOUvVbbuMnR3UR.txt", method = RequestMethod.GET)
	public String verify(HttpServletRequest request, HttpServletResponse response) {
		return "1yfOUvVbbuMnR3UR";
	}
	
	@ResponseBody
	@RequestMapping(value = "/notify")
    public String notify(HttpServletRequest request) throws IOException  {

        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();

        /** 支付成功后，微信回调返回的信息 */
        String result = new String(outSteam.toByteArray(), "utf-8");
        XmlMapper xml = new XmlMapper();
        Map<Object, Object> map = xml.readValue(result, Map.class);

        // 用于验签
        SortedMap<Object, Object> params = new TreeMap<Object, Object>();
        for (Object keyValue : map.keySet()) {
            /** 输出返回的订单支付信息 */
            if (!"sign".equals(keyValue)) {
            	params.put(keyValue, map.get(keyValue));
            }
        }
        if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
            // 先进行校验，是否是微信服务器返回的信息
            String checkSign = WeChatUtils.createSign("UTF-8", params);
            if (checkSign.equals(map.get("sign"))) {
            	// 如果签名和服务器返回的签名一致，说明数据没有被篡改过
                
                /** 告诉微信服务器，我收到信息了，不要再调用回调方法了 */
                /**如果不返回SUCCESS的信息给微信服务器，则微信服务器会在一定时间内，多次调用该回调方法，如果最终还未收到回馈，微信默认该订单支付失败*/
                /** 微信默认会调用8次该回调地址 */
            	return setXML("SUCCESS", ""); 
            }
        }
        return setXML("FAIL", "失败原因");
    }

    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
                + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
    }
}
