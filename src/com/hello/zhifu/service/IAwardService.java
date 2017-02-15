package com.hello.zhifu.service;

import java.util.List;

import com.hello.zhifu.model.Award;

public interface IAwardService {
	
	public Award current();
	
	public Award selectByPrimaryKey(Long termNum);
	
	public Integer insert(Award award);
	
	public Integer update(Award award);
	
	public List<Award> findList(String where, String order);
}
