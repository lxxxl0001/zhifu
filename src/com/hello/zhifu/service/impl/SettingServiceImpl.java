package com.hello.zhifu.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hello.zhifu.dao.FlowingMapper;
import com.hello.zhifu.dao.SettingMapper;
import com.hello.zhifu.model.Setting;
import com.hello.zhifu.service.ISettingService;

@Service
public class SettingServiceImpl implements ISettingService {

	@Autowired
	private SettingMapper settingMapper;
	@Autowired
	private FlowingMapper flowingMapper;
	
	@Override
	public Setting selectByPrimaryKey(Integer mkey) {
		return settingMapper.selectByPrimaryKey(mkey);
	}

	@Override
	public Integer update(Setting setting) {
		return settingMapper.update(setting);
	}

	@Override
	public List<Setting> findList(String where, String order) {
		return settingMapper.findList(where, order);
	}

	@Override
	public List<Setting> getNumberList(Long termNum) {
		Integer[] temNum = {1,2,3,4,5,6,7,8,9,10};
		
		List<Setting> awardNum = new ArrayList<Setting>();
		List<Setting> mkeyList = settingMapper.getNumberList(termNum);
		//没人买的放前面，默认：没人买为true，买了的为false
		for (Integer num : temNum) {
			Boolean isbuy = true;
			for (Setting mkey : mkeyList) {
				if(num == mkey.getMkey()){
					isbuy = false;
				}
			}
			if(isbuy){
				awardNum.add(new Setting(num, 0d));
			}
		}
		//购买为零的乱序
		Collections.shuffle(awardNum);
		//买了的放后面
		awardNum.addAll(mkeyList);

		//根据参数调整位置
		Setting key9 = settingMapper.selectByPrimaryKey(9);
		if (key9.getMvalue() == 20) {
			Collections.rotate(awardNum, 8);
		}
		if (key9.getMvalue() == 40) {
			Collections.rotate(awardNum, 6);
		}
		if (key9.getMvalue() == 60) {
			Collections.rotate(awardNum, 4);
		}
		if (key9.getMvalue() == 80) {
			Collections.rotate(awardNum, 2);
		}
		if (key9.getMvalue() == 100) {
			Collections.reverse(awardNum);
		}
		
		return awardNum;
	}

	/**
	 * 生成中奖号码
	 **/
	@Override
	public String getNumbers(Long termNum) {

		List<Setting> awardNum = getNumberList(termNum);
		//拼接中奖号码
		String awardNumbers = "";
		for (int i = 0; i < awardNum.size(); i++) {
			Integer element = awardNum.get(i).getMkey();
			awardNumbers+=","+element.toString();
		}
		awardNumbers = awardNumbers.substring(1);
		return awardNumbers;
	}
}
