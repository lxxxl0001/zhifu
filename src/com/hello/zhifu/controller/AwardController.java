package com.hello.zhifu.controller;

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

import com.hello.zhifu.model.Award;
import com.hello.zhifu.model.Flowing;
import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.service.IAwardService;
import com.hello.zhifu.service.IFlowingService;
import com.hello.zhifu.service.IUserInfoService;
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
	public Map<String, Object> getHistoryData(String date) {	
		Map<String, Object> data = new HashMap<String, Object>();
		List<Award> lishi = awardService.findList("awardDate like '"+date+"%'", "awardTime desc");
		data.put("rows", lishi);
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getAwardTimes.do", method = RequestMethod.GET)
	public Map<String, Object> getAwardTimes(Integer uid) {	
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
		data.put("current", current);
		//下次
		Map<String, Object> next = new HashMap<String, Object>();
		Date awardTime = DateUtils.getDate(award.getNextTime());
		next.put("awardTime", DateUtils.date2Str(awardTime,DateUtils.datetimeFormat));
		next.put("periodNumber", award.getTermNum()+1);
		//页面刷新不需要，开奖动画
		Long interval = award.getNextTime() - nowTime + 10 * 1000;
		next.put("awardTimeInterval", interval);

		data.put("next", next);
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getFlowing.do", method = RequestMethod.GET)
	public List<Flowing> getFlowing(Integer userid) {	
		List<Flowing> flow = flowService.findList("userid="+userid, "termNum desc");
		return flow==null? new ArrayList<Flowing>():flow;
	}
	
	@ResponseBody
	@RequestMapping(value = "/saveFlowing.do", method = RequestMethod.POST)
	public SortedMap<Object, Object> saveFlowing(Flowing flow, HttpServletRequest req) {	
		SortedMap<Object, Object> params = new TreeMap<Object, Object>();
		UserInfo user = userInfoService.selectByPrimaryKey(flow.getUserid());
		if(user!=null && flow.getBuyAmount()>0){
			Flowing poFlow = new Flowing();
			poFlow.setUserid(flow.getUserid());
			poFlow.setTermNum(flow.getTermNum());
			poFlow.setCarNum(flow.getCarNum());
			poFlow.setBuyAmount(flow.getBuyAmount());
			poFlow.setHaveAmount(0d);
			poFlow.setIsPay(0);
			poFlow.setIsOpen(0);
			poFlow.setIsSend(0);
			Map<String, Object> map = WeChatUtils.unorder(user.getOpenid(), 1, req.getRemoteAddr());
			if(map.get("result_code").toString().equalsIgnoreCase("SUCCESS")){
				flowService.insert(poFlow);
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
	            params.put("sendUrl", WeChatUtils.getDomain() + "pay/paysuccess?totalPrice=1");
	            /** 获取用户的微信客户端版本号，用于前端支付之前进行版本判断，微信版本低于5.0无法使用微信支付 */
	            String userAgent = req.getHeader("user-agent");
	            char agent = userAgent.charAt(userAgent.indexOf("MicroMessenger") + 15);
	            params.put("agent", new String(new char[] { agent }));
	            
			}
		}
		return params;
	}
}
