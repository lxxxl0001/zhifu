package com.hello.zhifu.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hello.zhifu.dao.FlowingMapper;
import com.hello.zhifu.dao.SettingMapper;
import com.hello.zhifu.model.Flowing;
import com.hello.zhifu.model.Setting;
import com.hello.zhifu.service.IFlowingService;

@Service
public class FlowingServiceImpl implements IFlowingService {

	@Autowired
	private FlowingMapper flowingMapper;
	@Autowired
	private SettingMapper settingMapper;
	
	@Override
	public Flowing selectByPrimaryKey(Long flowid) {
		return flowingMapper.selectByPrimaryKey(flowid);
	}

	@Override
	public Integer insert(Flowing flowing) {
		return flowingMapper.insert(flowing);
	}

	@Override
	public Integer update(Flowing flowing) {
		return flowingMapper.update(flowing);
	}

	@Override
	public List<Flowing> findList(String where, String order) {
		return flowingMapper.findList(where, order);
	}

	@Override
	public Map<String, Object> getNumberMap(Long termNum) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = flowingMapper.getNumberMap(termNum);
		for (Map<String, Object> map : list) {
			String mkey = null;
			Object mvalue = null;
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if("carNum".equals(entry.getKey())){
					mkey = entry.getValue().toString();
				}
				if("buyAmount".equals(entry.getKey())){
					mvalue = entry.getValue().toString();
				}
			}
			resultMap.put(mkey,mvalue);
		}
		return resultMap;
	}

	@Override
	public List<String> getNumberList(Long termNum) {
		String[] temNum = {"1","2","3","4","5","6","7","8","9","10"};
		
		List<String> awardNum = new ArrayList<String>();
		Map<String, Object> mkey = getNumberMap(termNum);
		//购买为零的放前面，没取到即购买为零
		for (String num : temNum) {
			if(!mkey.containsKey(num)){
				awardNum.add(num);
			}
		}
		//购买为零的乱序
		Collections.shuffle(awardNum);
		//买了的，从小到大
		for (Map.Entry<String, Object> entry : mkey.entrySet()) {
			awardNum.add(entry.getKey().toString());
		}
		
		//根据参数调整位置
		Setting key9 = settingMapper.selectByPrimaryKey(9);
		
		key9.setMvalue(0d);
		settingMapper.update(key9);
		
		return awardNum;
	}
	
	/**
	 * 计算中级金额并生成中奖号码
	 **/
	@Override
	public String getNumbers(Long termNum) {
		
		List<String> awardNum = getNumberList(termNum);
		
		String awardNumbers = "";
		for (int i = 0; i < awardNum.size(); i++) {
			String element = awardNum.get(i);
			//计算中奖金额//+" and isPay=1"
			List<Flowing> flowlist = findList("termNum="+termNum+" and carNum="+element, null);
			for (Flowing flow : flowlist) {
				//只计算前五名
				calcAmount(flow, i + 1);
				update(flow);
			}
			//拼接中奖号码
			awardNumbers+=","+element.toString();
		}
		awardNumbers = awardNumbers.substring(1);
		return awardNumbers;
	}
	
	private void calcAmount(Flowing flow,Integer num){
		if (num < 5) {
			Setting key = settingMapper.selectByPrimaryKey(num);
			Double s = flow.getBuyAmount() * key.getMvalue();
			flow.setHaveAmount(s.intValue());
			flow.setIsOpen(1);
		}
	}
}
