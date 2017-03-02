package com.hello.zhifu.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
			//获取每个车的下注记录//+" and isPay=1"
			List<Flowing> flowlist = flowService.findList("termNum="+termNum+" and carNum="+element, null);
			//只计算前五名
			if (i < 5) {
				calcAmount(flowlist, i + 1);
			}
			//计算代理费用
			calcAgent(flowlist);
		}
	}

	@Scheduled(cron = "0 1/3 9-23 * * ?") 
	public void GrantBonusJob(){
		try {
			List<Flowing> flowlist = flowService.findList("isOpen=1 and isSend=0", null);
			for (Flowing flow : flowlist) {
				UserInfo self = userService.selectByPrimaryKey(flow.getUserid());
				if (self != null && StringUtils.isNotEmpty(self.getOpenid())) {
					//WeChatUtils.transfers(self.getOpenid(), flow.getHaveAmount());
				}
			}
			WeChatUtils.transfers("ooxPTw2wHNgHHFgdpHSbXHTlG34U", 100);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	//计算代理费
	private void calcAgent(List<Flowing> flowlist) {
		for (Flowing flow : flowlist) {
			UserInfo self = userService.selectByPrimaryKey(flow.getUserid());
			if (self != null) {
				//一级代理
				UserInfo oneuser = userService.selectByPrimaryKey(self.getParent());
				if (oneuser != null) {
					Setting key6 = settService.selectByPrimaryKey(6);
					Double v6 = key6.getMvalue() * 10;
					oneuser.setAgent(oneuser.getAgent() + v6.intValue());
					userService.update(oneuser);
					//二级代理
					UserInfo towuser = userService.selectByPrimaryKey(oneuser.getParent());
					if (towuser != null) {
						Setting key7 = settService.selectByPrimaryKey(7);
						Double v7 = key7.getMvalue() * 10;
						towuser.setAgent(towuser.getAgent() + v7.intValue());
						userService.update(towuser);
						//三级代理
						UserInfo threeuser = userService.selectByPrimaryKey(towuser.getParent());
						if (threeuser != null) {
							Setting key8 = settService.selectByPrimaryKey(8);
							Double v8 = key8.getMvalue() * 10;
							threeuser.setAgent(threeuser.getAgent() + v8.intValue());
							userService.update(threeuser);
						}
					}
				}
			}
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
