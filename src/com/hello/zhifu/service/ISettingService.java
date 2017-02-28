package com.hello.zhifu.service;

import java.util.List;

import com.hello.zhifu.model.Setting;

public interface ISettingService {

	public Setting selectByPrimaryKey(Integer mkey);
	
	public Integer update(Setting setting);
	
	public List<Setting> findList(String where, String order);
	
	public List<Setting> getNumberList(Long termNum);
	
	public String getNumbers(Long termNum);
}
