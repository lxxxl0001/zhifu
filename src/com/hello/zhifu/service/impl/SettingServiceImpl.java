package com.hello.zhifu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hello.zhifu.dao.SettingMapper;
import com.hello.zhifu.model.Setting;
import com.hello.zhifu.service.ISettingService;

public class SettingServiceImpl implements ISettingService {

	@Autowired
	private SettingMapper settingMapper;
	
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

}
