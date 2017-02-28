package com.hello.zhifu.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class WeChatUtils {

	private static final String domain = SettingsUtil.getInstance().getString("domain");
	
	private static final String appid = SettingsUtil.getInstance().getString("wx.appid");

	private static final String appsecret = SettingsUtil.getInstance().getString("wx.appsecret");
	
	private static final String mchid = SettingsUtil.getInstance().getString("wx.mchid");
	
	private static final String apikey = SettingsUtil.getInstance().getString("wx.apikey");
	
	private static final HttpClient client = new HttpClient();
	
	public static String getDomain() {
		return domain;
	}

	public static String getAppid() {
		return appid;
	}

	public static String getAppsecret() {
		return appsecret;
	}

	public static String oAuth2Url(String returnUrl){
		//api
		String oauth2url = "https://open.weixin.qq.com/connect/oauth2/authorize";
		
		String paramet = "?appid=" + appid
				+ "&redirect_uri=" + URLEncoder.encode(returnUrl)
				+ "&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
		return oauth2url + paramet;
	}
	
	public static String getOpenId(String code) {
		//api
		String tokenurl = "https://api.weixin.qq.com/sns/oauth2/access_token"; 
		
		String openId = null;
		
		if (code != null) {

			String paramet = "?appid=" + appid + "&secret=" + appsecret
					+ "&code=" + code + "&grant_type=authorization_code";

			// 设置代理服务器地址和端口
			// client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
			// 使用 GET 方法 ，如果服务器需要通过 HTTPS 连接，那只需要将下面 URL 中的 http 换成 https
			GetMethod method = new GetMethod(tokenurl + paramet);
			// 使用POST方法
			// HttpMethod method = new PostMethod("http://java.sun.com");
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				client.executeMethod(method);
				client.setTimeout(3000);
				//获取封装返回的信息
				ObjectMapper objectMapper = new ObjectMapper();
				map = objectMapper.readValue(method.getResponseBodyAsString(), Map.class);
				//获取openId
				openId = map.get("openid").toString();
				
			} catch (Exception e) {
				// e.printStackTrace();
			}

			// 释放连接
			method.releaseConnection();
		}
		
		return openId; 
	}
	
	public static Map<String, Object> unorder(String openId, Integer totalFee, String createIp) {
		//api
		String unifiedorder = "https://api.mch.weixin.qq.com/pay/unifiedorder";

		Map<String, Object> map = new HashMap<String, Object>();
		
		SortedMap<Object, Object> params = new TreeMap<Object, Object>();
        //** 公众号APPID *//*
		params.put("appid", appid);
        //** 商户号 *//*
		params.put("mch_id", mchid);
        //** 随机字符串 *//*
		params.put("nonce_str", getNonceStr());
        //** 商品名称 *//*
		params.put("body", "测试");
        //** 订单号 *//*
        params.put("out_trade_no", DateUtils.getMillis()+""+buildRandom(4));
        //** 订单金额以分为单位，只能为整数 *//*
        params.put("total_fee", totalFee);
        //** 客户端本地ip *//*
        params.put("spbill_create_ip", createIp);
        //** 支付回调地址 *//*
        params.put("notify_url", domain+"/notify");
        //** 支付方式为JSAPI支付 *//*
        params.put("trade_type", "JSAPI");
        //** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 *//*
        params.put("openid", openId);
        
        //** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 *//*
        String sign = createSign("UTF-8", params);
        params.put("sign", sign);
		
		PostMethod method = new PostMethod(unifiedorder);
		client.getParams().setSoTimeout(300 * 1000);
		try {
			//xml参数
			XmlMapper xml = new XmlMapper();
			String paramXML = xml.writeValueAsString(params);
			
			method.setRequestEntity(new StringRequestEntity(paramXML, "text/xml", "utf-8"));
			client.executeMethod(method);
			client.setTimeout(3000);
			// 打印服务器返回的状态
			System.out.println(method.getStatusLine());
			// 打印返回的信息
			byte[] b = method.getResponseBody();
			String response = new String(b, "utf-8");
			System.out.println(response);
			//解析xml
			map = xml.readValue(response, Map.class);
			
		} catch (Exception e) {
			// e.printStackTrace();
		}

		// 释放连接
		method.releaseConnection();
		
		return map;
	}

	public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
    }

	public static String createSign(String characterEncoding,
			SortedMap<Object, Object> parameters) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<Object, Object>> es = parameters.entrySet();
		Iterator<Entry<Object, Object>> it = es.iterator();
		while (it.hasNext()) {
			Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			// ** 如果参数为key或者sign，则不参与加密签名 *//*
			if (null != v && !"".equals(v) && !"sign".equals(k)	&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		// ** 支付密钥必须参与加密，放在字符串最后面 *//*
		sb.append("key=" + apikey);
		// ** 记得最后一定要转换为大写 *//*
		String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
		return sign;
	}
	
	public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }
}
