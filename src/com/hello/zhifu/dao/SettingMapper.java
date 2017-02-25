package com.hello.zhifu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hello.zhifu.model.Setting;

public interface SettingMapper {

	public Setting selectByPrimaryKey(Integer mkey);
	
	public Integer update(Setting setting);
	
	public List<Setting> findList(@Param("where")String where, @Param("order")String order);
}
