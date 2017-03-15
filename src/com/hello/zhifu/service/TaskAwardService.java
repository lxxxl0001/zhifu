package com.hello.zhifu.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hello.zhifu.model.Award;
import com.hello.zhifu.model.Flowing;
import com.hello.zhifu.model.Setting;
import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.utils.DateUtils;
import com.hello.zhifu.utils.WeChatUtils;

@Component("taskAward")
public class TaskAwardService {

	@Autowired
	private IAwardService awardService;
	@Autowired
	private IFlowingService flowService;
	@Autowired
	private ISettingService settService;
	@Autowired
	private IUserInfoService userService;
	
	@Scheduled(cron = "0 0/3 9-23 * * ?") 
	public void DrawAwardJob(){
		//系统当前时间
		Long awardTime = System.currentTimeMillis();
		//获取短时间HH:mm
		String shortTime = DateUtils.formatShortTime(awardTime);
		//每天9点开始12点结束
		if("09:00".equals(shortTime)){
			return;
		}
		//下次开奖时间
		Long nextTime = awardTime + 3 * 60 * 1000;
		String nextShort = DateUtils.formatShortTime(nextTime);
		Integer sTime = Integer.parseInt(nextShort.replace(":", ""));
		if(sTime < 900){
			nextTime = awardTime + 546 * 60 * 1000;
		}
		//开奖
		Award current = awardService.current();
		Long termNum = current == null ? 1 : current.getTermNum() + 1;
		String awardNumbers = settService.getNumbers(termNum);
		
		//生成开奖记录
		Timestamp awardDate = DateUtils.getTimestamp(awardTime);
		Award nextAward = new Award();
		nextAward.setAwardDate(awardDate);
		nextAward.setAwardTime(awardTime);
		nextAward.setNextTime(nextTime);
		nextAward.setAwardNumbers(awardNumbers);
		awardService.insert(nextAward);
		
		//计算中奖金额
		String[] awardNum = awardNumbers.split(",");
		for (int i = 0; i < awardNum.length; i++) {
			String element = awardNum[i];
			//获取每个车的下注记录//
			List<Flowing> flowlist = flowService.findList("termNum="+termNum+" and carNum="+element+" and isPay=1", null);
			//只计算前五名
			if (i < 5) {
				calcAmount(flowlist, i + 1);
			}
		}
	}

	@Scheduled(cron = "30 0/3 9-23 * * ?") 
	public void GrantBonusJob(){
		Long nowTime = System.currentTimeMillis();
		String nextShort = DateUtils.formatShortTime(nowTime);
		Integer sTime = Integer.parseInt(nextShort.replace(":", ""));
		if(sTime < 903){
			return;
		}
		try {
			//企业付款标记
			Setting key10 = settService.selectByPrimaryKey(10);
			Map<String, Object> map = new HashMap<String, Object>();
			if (key10 != null && key10.getMvalue() == 0) {
				//获取用户中奖金额
				List<Flowing> flowlist = flowService.findHaveAmount();
				for (Flowing flow : flowlist) {
					UserInfo self = userService.selectByPrimaryKey(flow.getUserid());
					if (self != null) {
						map = WeChatUtils.transfers(self.getOpenid(), flow.getHaveAmount()*100);
						if("SUCCESS".equals(map.get("result_code"))){
							flowService.updateIsSend(self.getUserid());
						}
						//余额不足，关闭企业付款
						if("FAIL".equals(map.get("result_code"))){
							key10.setMvalue(1d);
							settService.update(key10);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//计算中奖金额
	private void calcAmount(List<Flowing> flowlist, Integer mkey) {
		Setting key = settService.selectByPrimaryKey(mkey);
		for (Flowing flow : flowlist) {
			Double s = flow.getBuyAmount() * key.getMvalue();
			flow.setHaveAmount(s.intValue());
			flow.setIsOpen(1);
			flowService.update(flow);
		}
	}
}
