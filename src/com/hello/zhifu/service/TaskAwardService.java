package com.hello.zhifu.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hello.zhifu.model.Award;
import com.hello.zhifu.utils.DateUtils;

@Component("taskAward")
public class TaskAwardService {

	@Autowired
	private IAwardService awardService;
	@Autowired
	private ISettingService settingService;
	
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
		Long termNum = awardService.current().getTermNum();
		String awardNumbers = settingService.getNumbers(termNum + 1);
		
		//开奖时间 带年月日 方便查询
		Timestamp awardDate = DateUtils.getTimestamp(awardTime);
		
		//生成开奖记录
		Award current = new Award();
		current.setAwardDate(awardDate);
		current.setAwardTime(awardTime);
		current.setNextTime(nextTime);
		current.setAwardNumbers(awardNumbers);
		awardService.insert(current);
	}
	
	
}
