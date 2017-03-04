package com.hello.zhifu.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hello.zhifu.model.Award;
import com.hello.zhifu.model.Flowing;
import com.hello.zhifu.model.Setting;
import com.hello.zhifu.service.IAwardService;
import com.hello.zhifu.service.IFlowingService;
import com.hello.zhifu.service.ISettingService;
import com.hello.zhifu.service.IUserInfoService;
import com.hello.zhifu.utils.CookieUtils;
import com.hello.zhifu.utils.DateUtils;
import com.hello.zhifu.utils.WeChatUtils;

@Controller
public class AwardController {

	@Autowired
	private IAwardService awardService;
	@Autowired
	private IFlowingService flowService;
	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private ISettingService settingService;
	
	@ResponseBody
	@RequestMapping(value = "/getData.do", method = RequestMethod.GET)
	public Map<String, Object> getData() {	
		Map<String, Object> data = new HashMap<String, Object>();
		Long nowTime = System.currentTimeMillis();
		data.put("time", nowTime);
		data.put("firstPeriod", "596772");//当天第一期期号
		data.put("apiVersion", 1);
		//当前
		Award award = awardService.current();
		Map<String, Object> current = new HashMap<String, Object>();
		current.put("awardTime", DateUtils.date2Str(award.getAwardDate(),DateUtils.datetimeFormat));
		current.put("periodNumber", award.getTermNum());
		current.put("fullPeriodNumber", award.getTermNum());
		current.put("periodNumberStr",  award.getTermNum().toString());
		current.put("awardTimeInterval", 0);
		current.put("awardNumbers", award.getAwardNumbers());
		current.put("delayTimeInterval", null);
		current.put("pan", null);
		current.put("isEnd", null);
		current.put("nextMinuteInterval", null);
		data.put("current", current);
		//下次
		Map<String, Object> next = new HashMap<String, Object>();
		Date awardTime = DateUtils.getDate(award.getNextTime());
		next.put("awardTime", DateUtils.date2Str(awardTime,DateUtils.datetimeFormat));
		next.put("periodNumber", award.getTermNum()+1);
		next.put("fullPeriodNumber", null);
		next.put("periodNumberStr", null);
		//提前一分钟开始，开奖动画
		Long interval = award.getNextTime() - nowTime;
		next.put("awardTimeInterval", interval);
		next.put("awardNumbers", null);
		next.put("delayTimeInterval", null);
		next.put("pan", null);
		next.put("isEnd", null);
		next.put("nextMinuteInterval", null);
		data.put("next", next);
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getHistoryData.do", method = RequestMethod.GET)
	public List<Award> getHistoryData(String date) {	
		return awardService.findList("awardDate like '"+date+"%'", "awardTime desc");
	}
	
	@ResponseBody
	@RequestMapping(value = "/getAwardTimes.do", method = RequestMethod.GET)
	public Map<String, Object> getAwardTimes() {	
		Map<String, Object> data = new HashMap<String, Object>();
		Long nowTime = System.currentTimeMillis();
		//当前
		Award award = awardService.current();
		Map<String, Object> current = new HashMap<String, Object>();
		current.put("awardTime", DateUtils.date2Str(award.getAwardDate(),DateUtils.datetimeFormat));
		current.put("periodNumber", award.getTermNum());
		current.put("awardNumbers", award.getAwardNumbers());
		data.put("current", current);
		//下次
		Map<String, Object> next = new HashMap<String, Object>();
		next.put("periodNumber", award.getTermNum()+1);
		//页面刷新不需要，开奖动画
		Long interval = award.getNextTime() - nowTime;
		next.put("awardTimeInterval", interval > 0 ? interval : 0);
		data.put("next", next);
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getFlowing.do", method = RequestMethod.GET)
	public List<Flowing> getFlowing(Integer userid) {	
		List<Flowing> flow = flowService.findList("userid="+userid+" and isPay=1", "flowid desc");
		return flow==null? new ArrayList<Flowing>():flow;
	}
	
	@ResponseBody
	@RequestMapping(value = "/saveFlowing.do", method = RequestMethod.POST)
	public SortedMap<Object, Object> saveFlowing(Flowing flow, HttpServletRequest req) {	
		SortedMap<Object, Object> params = new TreeMap<Object, Object>();
		String openId = CookieUtils.getCookieValue(req, "openId");
		if (flow != null && flow.getBuyAmount() > 0) {
			int totalFee = flow.getBuyAmount()*100;
			int rand = WeChatUtils.buildRandom(3);
			String trade_no = DateUtils.getMillis()+""+rand;
			Map<String, Object> map = WeChatUtils.unorder(openId, trade_no, totalFee, req.getRemoteAddr());
			if(map.get("result_code").toString().equalsIgnoreCase("SUCCESS")){
				Flowing poFlow = new Flowing();
				poFlow.setFlowid(Long.parseLong(trade_no));
				poFlow.setUserid(flow.getUserid());
				poFlow.setTermNum(flow.getTermNum());
				poFlow.setCarNum(flow.getCarNum());
				poFlow.setBuyAmount(flow.getBuyAmount());
				poFlow.setHaveAmount(0);
				poFlow.setIsPay(0);
				poFlow.setIsOpen(0);
				poFlow.setIsSend(0);
				flowService.insert(poFlow);
				
				//封装前台参数
	            params.put("appId", WeChatUtils.getAppid());
	            params.put("timeStamp", ""+DateUtils.getMillis()/1000);
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
	            String paySign = WeChatUtils.createSign("UTF-8", params);
	            params.put("paySign", paySign);
	            /** 预支付单号，前端ajax回调获取。由于js中package为关键字，所以，这里使用packageValue作为key。 */
	            params.put("packageValue", "prepay_id=" + map.get("prepay_id"));
	            /** 付款成功后，微信会同步请求我们自定义的成功通知页面，通知用户支付成功 */
	            params.put("sendUrl", WeChatUtils.getDomain() + "paysuccess");
	            /** 获取用户的微信客户端版本号，用于前端支付之前进行版本判断，微信版本低于5.0无法使用微信支付 */
	            String userAgent = req.getHeader("user-agent");
	            char agent = userAgent.charAt(userAgent.indexOf("MicroMessenger") + 15);
	            params.put("agent", new String(new char[] { agent }));
			}
		}
		return params;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getNumbers.do", method = RequestMethod.GET)
	public List<Setting> getFlowing(Long termNum) {	
		List<Setting> nums = settingService.getNumberList(termNum);
		return nums==null? new ArrayList<Setting>():nums;
	}
	
	@ResponseBody
	@RequestMapping(value = "/savevalue.do", method = RequestMethod.POST)
	public Boolean savevalue(Setting data) {
		return settingService.update(data) > 0;
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
                /** 如果不返回SUCCESS的信息给微信服务器，则微信服务器会在一定时间内，多次调用该回调方法，如果最终还未收到回馈，微信默认该订单支付失败*/
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
