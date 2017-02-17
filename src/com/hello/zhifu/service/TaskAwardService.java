package com.hello.zhifu.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hello.zhifu.model.Award;
import com.hello.zhifu.model.Flowing;
import com.hello.zhifu.utils.DateUtils;

@Component("taskAward")
public class TaskAwardService {

	@Autowired
	private IAwardService awardService;
	@Autowired
	private IFlowingService flowService;
	
	private List<Integer> awardNum;
	
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
		String awardNumbers = getNumbers(termNum+1);
		
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
	
	private String getNumbers(Long termNum) {
		awardNum = new ArrayList<Integer>();
		awardNum.add(1);
		awardNum.add(2);
		awardNum.add(3);
		awardNum.add(4);
		awardNum.add(5);
		awardNum.add(6);
		awardNum.add(7);
		awardNum.add(8);
		awardNum.add(9);
		awardNum.add(10);
		Collections.shuffle(awardNum);
		String awardNumbers = "";
		for (int i = 0; i < awardNum.size(); i++) {
			Integer element = awardNum.get(i);
			awardNumbers+=","+element.toString();
			
			List<Flowing> flowlist = flowService.findList("termNum="+termNum+" and carNum="+element+" and isPay=1", null);
			for (Flowing flow : flowlist) {
				if (i == 0) {
					flow.setIsOpen(1);
					flow.setHaveAmount(flow.getBuyAmount()*2);
				}
				if (i == 1) {
					flow.setIsOpen(1);
					flow.setHaveAmount(flow.getBuyAmount()*1.8);
				}
				if (i == 2) {
					flow.setIsOpen(1);
					flow.setHaveAmount(flow.getBuyAmount()*1.6);
				}
				if (i == 3) {
					flow.setIsOpen(1);
					flow.setHaveAmount(flow.getBuyAmount()*1.4);
				}
				if (i == 4) {
					flow.setIsOpen(1);
					flow.setHaveAmount(flow.getBuyAmount()*1);
				}
				flowService.update(flow);
			}
		}
		awardNumbers = awardNumbers.substring(1);
		return awardNumbers;
	}
}
