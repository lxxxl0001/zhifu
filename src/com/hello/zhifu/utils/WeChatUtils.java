package com.hello.zhifu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class WeChatUtils {

	private static final String domain = SettingsUtil.getInstance().getString("domain");
	
	private static final String appid = SettingsUtil.getInstance().getString("wx.appid");

	private static final String appsecret = SettingsUtil.getInstance().getString("wx.appsecret");
	
	private static final String mchid = SettingsUtil.getInstance().getString("wx.mchid");
	
	private static final String apikey = SettingsUtil.getInstance().getString("wx.apikey");
	
	private static final String certfile = SettingsUtil.getInstance().getString("certfile");
	
	private static final String apiip = SettingsUtil.getInstance().getString("apiip");
	
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
				// 打印服务器返回的状态
				System.out.println(method.getStatusLine());
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
	
	public static Map<String, Object> unorder(String openId, String trade_no, Integer totalFee, String createIp) {
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
        params.put("out_trade_no", trade_no);
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
			//解析xml
			map = xml.readValue(response, Map.class);
			
		} catch (Exception e) {
			// e.printStackTrace();
		}

		// 释放连接
		method.releaseConnection();
		
		return map;
	}

	public static Map<String, Object> transfers(String openId, Integer amount) throws Exception {
		// api
		String transfers = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

		Map<String, Object> map = new HashMap<String, Object>();

		SortedMap<Object, Object> params = new TreeMap<Object, Object>();
		// ** 公众号APPID *//*
		params.put("mch_appid", appid);
		// ** 商户号 *//*
		params.put("mchid", mchid);
		// ** 随机字符串 *//*
		params.put("nonce_str", getNonceStr());
		// ** 商户订单号 *//*
		params.put("partner_trade_no", DateUtils.getMillis() + "" + buildRandom(4));
		// ** 用户微信的openid *//*
		params.put("openid", openId);
		// ** 校验用户姓名 *//*
		params.put("check_name", "NO_CHECK");
		// ** 企业付款金额，单位为分 *//*
		params.put("amount", amount);
		// ** 付款描述信息 *//*
		params.put("desc", "退货补偿");
		// ** 客户端本地ip *//*
		params.put("spbill_create_ip", apiip);

		// ** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 *//*
		String sign = createSign("UTF-8", params);
		params.put("sign", sign);
		
		
		CloseableHttpClient httpclient = getClient(certfile, mchid);

		try {
			// xml参数
			XmlMapper xml = new XmlMapper();
			String paramXML = xml.writeValueAsString(params);

			HttpPost method = new HttpPost(transfers);
			method.setEntity(new StringEntity(paramXML, "UTF-8"));

			CloseableHttpResponse response = httpclient.execute(method);

			try {
				String resStr = EntityUtils.toString(response.getEntity(), "UTF-8");

				System.out.println(resStr);
				// 解析xml
				map = xml.readValue(resStr, Map.class);

			} finally {
				response.close();
			}

		} finally {
			httpclient.close();
		}
		
		return map;
	}
	
	public static CloseableHttpClient getClient(String file, String mchid) throws Exception {
		// 指定读取证书格式为PKCS12
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		// 读取本机存放的PKCS12证书文件
		FileInputStream instream = new FileInputStream(new File(file));
		try {
			// 指定PKCS12的密码(商户ID)
			keyStore.load(instream, mchid.toCharArray());
		} catch (Exception e) {
			System.out.println(file + "证书不存在");
		} finally {
			instream.close();
		}
		SSLContext sslcontext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, mchid.toCharArray()).build();
		// 指定TLS版本
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		// 设置httpclient的SSLSocketFactory
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf).build();

		return httpclient;
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
