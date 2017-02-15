package com.hello.zhifu.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.DateUtils;
import com.hello.zhifu.utils.MD5Util;
import com.hello.zhifu.utils.SettingsUtil;
import com.hello.zhifu.utils.WeChatUtils;

@Controller
@RequestMapping("/jsapi")
public class TestPayController {
	
	private static final String appid = SettingsUtil.getInstance().getString("wx.appid");

	private static final String mchid = SettingsUtil.getInstance().getString("wx.mchid");
	
	private static final String apikey = SettingsUtil.getInstance().getString("wx.apikey");
	
	@SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping(value = "gopay",method = RequestMethod.POST)
    public SortedMap<Object, Object> Gopay(HttpServletRequest request, String commodityName, double totalPrice) {
        
        String openId = CookieUtils.getCookieValue(request, "openId");
        /** 总金额(分为单位) */
        int total = (int) (totalPrice * 100);

        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        /** 公众号APPID */
        parameters.put("appid", appid);
        /** 商户号 */
        parameters.put("mch_id", mchid);
        /** 随机字符串 */
        parameters.put("nonce_str", WeChatUtils.getNonceStr());
        /** 商品名称 */
        parameters.put("body", commodityName);

        /** 当前时间 yyyyMMddHHmmss */
        String currTime = DateUtils.getDate("yyyymmddhhmmss");
        /** 四位随机数 */
        String strRandom = WeChatUtils.buildRandom(4) + "";
        /** 订单号 */
        parameters.put("out_trade_no", currTime + strRandom);
        
        /** 订单金额以分为单位，只能为整数 */
        parameters.put("total_fee", total);
        /** 客户端本地ip */
        parameters.put("spbill_create_ip", request.getRemoteAddr());
        /** 支付回调地址 */
        parameters.put("notify_url", WeChatUtils.getDomain() + "/pay");
        /** 支付方式为JSAPI支付 */
        parameters.put("trade_type", "JSAPI");
        /** 用户微信的openid，当trade_type为JSAPI的时候，该属性字段必须设置 */
        parameters.put("openid", openId);
        
        /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
        String sign = createSign("UTF-8", parameters);
        parameters.put("sign", sign);
        
        /** 生成xml结构的数据，用于统一下单接口的请求 */
        String requestXML = getRequestXml(parameters);

        String unifiedorder = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        /** 开始请求统一下单接口，获取预支付prepay_id */
        HttpClient client = new HttpClient();
        PostMethod myPost = new PostMethod(unifiedorder);
        client.getParams().setSoTimeout(300 * 1000);
        String result = null;
        try {
            myPost.setRequestEntity(new StringRequestEntity(requestXML, "text/xml", "utf-8"));
            int statusCode = client.executeMethod(myPost);
            if (statusCode == HttpStatus.SC_OK) {
                BufferedInputStream bis = new BufferedInputStream(myPost.getResponseBodyAsStream());
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int count = 0;
                while ((count = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, count);
                }
                byte[] strByte = bos.toByteArray();
                result = new String(strByte, 0, strByte.length, "utf-8");
                System.out.println(result);
                bos.close();
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /** 需要释放掉、关闭连接 */
        myPost.releaseConnection();
        client.getHttpConnectionManager().closeIdleConnections(0);

        SortedMap<Object, Object> params = new TreeMap<Object, Object>();
        try {
            /** 解析微信返回的信息，以Map形式存储便于取值 */
        	XmlMapper xml = new XmlMapper();
            Map<String, String> map = xml.readValue(result, Map.class);
            
            params.put("appId", appid);
            params.put("timeStamp", "\"" + new Date().getTime() + "\"");
            params.put("nonceStr", WeChatUtils.getNonceStr());
            /** 
             * 获取预支付单号prepay_id后，需要将它参与签名。
             * 微信支付最新接口中，要求package的值的固定格式为prepay_id=... 
             */
            params.put("package", "prepay_id=" + map.get("prepay_id"));

            /** 微信支付新版本签名算法使用MD5，不是SHA1 */
            params.put("signType", "MD5");
            /**
             * 获取预支付prepay_id之后，需要再次进行签名，参与签名的参数有：appId、timeStamp、nonceStr、package、signType.
             * 主意上面参数名称的大小写.
             * 该签名用于前端js中WeixinJSBridge.invoke中的paySign的参数值
             */
            String paySign = createSign("UTF-8", params);
            params.put("paySign", paySign);
            
            /** 预支付单号，前端ajax回调获取。由于js中package为关键字，所以，这里使用packageValue作为key。 */
            params.put("packageValue", "prepay_id=" + map.get("prepay_id"));
            
            /** 获取用户的微信客户端版本号，用于前端支付之前进行版本判断，微信版本低于5.0无法使用微信支付 */
            String userAgent = request.getHeader("user-agent");
            char agent = userAgent.charAt(userAgent.indexOf("MicroMessenger") + 15);
            params.put("agent", new String(new char[] { agent }));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            /** 如果参数为key或者sign，则不参与加密签名 */
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        /** 支付密钥必须参与加密，放在字符串最后面 */
        sb.append("key=" + apikey);
        /** 记得最后一定要转换为大写 */
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
            String k = (String) entry.getKey();
            String v = entry.getValue() + "";
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "pay")
    public @ResponseBody void notify_success(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
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
        Map<String, String> map = xml.readValue(result, Map.class);

        // 用于验签
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        for (Object keyValue : map.keySet()) {
            /** 输出返回的订单支付信息 */
            if (!"sign".equals(keyValue)) {
                parameters.put(keyValue, map.get(keyValue));
            }
        }
        if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
            // 先进行校验，是否是微信服务器返回的信息
            String checkSign = createSign("UTF-8", parameters);

            if (checkSign.equals(map.get("sign"))) {// 如果签名和服务器返回的签名一致，说明数据没有被篡改过
                
                /** 告诉微信服务器，我收到信息了，不要再调用回调方法了 */
                /**如果不返回SUCCESS的信息给微信服务器，则微信服务器会在一定时间内，多次调用该回调方法，如果最终还未收到回馈，微信默认该订单支付失败*/
                /** 微信默认会调用8次该回调地址 */
                response.getWriter().write(setXML("SUCCESS", ""));
                
            }
        }
    }

    public static String setXML(String return_code, String return_msg) {
        return "<xml><return_code><![CDATA[" + return_code
                + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
    }
}
