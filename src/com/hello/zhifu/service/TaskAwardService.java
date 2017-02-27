package com.hello.zhifu.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hello.zhifu.model.Award;
import com.hello.zhifu.model.Flowing;
import com.hello.zhifu.model.Setting;
import com.hello.zhifu.utils.DateUtils;

@Component("taskAward")
public class TaskAwardService {

	@Autowired
	private IAwardService awardService;
	@Autowired
	private IFlowingService flowService;
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
		
		Setting key1 = settingService.selectByPrimaryKey(1);
		Setting key2 = settingService.selectByPrimaryKey(2);
		Setting key3 = settingService.selectByPrimaryKey(3);
		Setting key4 = settingService.selectByPrimaryKey(4);
		Setting key5 = settingService.selectByPrimaryKey(5);
		
		Integer[] temp = {1,2,3,4,5,6,7,8,9,10};
		List<Integer> temNum = Arrays.asList(temp);
		
		List<Integer> awardNum = new ArrayList<Integer>();
		Map<Integer, Integer> rNumber = flowService.getNumberMap(termNum);
		//移除买了的没买的放前面
		for (Map.Entry<Integer, Integer> entry : rNumber.entrySet()) {
			temNum.remove(entry.getKey());
		}
		Collections.shuffle(temNum);
		//购买为零的放前面
		awardNum.addAll(temNum);
		for (Map.Entry<Integer, Integer> entry : rNumber.entrySet()) {
			awardNum.add(entry.getKey());
		}
		
		//根据参数调整位置
		Setting key9 = settingService.selectByPrimaryKey(9);
		key9.setMvalue(0d);
		settingService.update(key9);
		
		
		String awardNumbers = "";
		for (int i = 0; i < awardNum.size(); i++) {
			Integer element = awardNum.get(i);
			//计算中奖金额
			List<Flowing> flowlist = flowService.findList("termNum="+termNum+" and carNum="+element+" and isPay=1", null);
			for (Flowing flow : flowlist) {
				if (i == 0) {
					flow.setIsOpen(1);
					flow.setHaveAmount(calcAmount(flow.getBuyAmount(),key1.getMvalue()));
				}
				if (i == 1) {
					flow.setIsOpen(1);
					flow.setHaveAmount(calcAmount(flow.getBuyAmount(),key2.getMvalue()));
				}
				if (i == 2) {
					flow.setIsOpen(1);
					flow.setHaveAmount(calcAmount(flow.getBuyAmount(),key3.getMvalue()));
				}
				if (i == 3) {
					flow.setIsOpen(1);
					flow.setHaveAmount(calcAmount(flow.getBuyAmount(),key4.getMvalue()));
				}
				if (i == 4) {
					flow.setIsOpen(1);
					flow.setHaveAmount(calcAmount(flow.getBuyAmount(),key5.getMvalue()));
				}
				flowService.update(flow);
			}
			//拼接中奖号码
			awardNumbers+=","+element.toString();
		}
		awardNumbers = awardNumbers.substring(1);
		return awardNumbers;
	}
	
	private Integer calcAmount(Integer buy,Double rate){
		Double s = 0d;
		s = buy * rate;
		return s.intValue();
	}
}
