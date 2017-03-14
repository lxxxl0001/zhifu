package com.hello.zhifu.service;

import java.util.List;

import com.hello.zhifu.model.Flowing;

public interface IFlowingService {
	
	public Flowing selectByPrimaryKey(Long flowid);
	
	public Integer insert(Flowing flowing);
	
	public Integer update(Flowing flowing);
	
	public List<Flowing> findList(String where, String order);
	
	public List<Flowing> findHaveAmount();
	
	public Integer updateIsSend(Integer userid);
	
	public Integer getCount(String where);
	
	public List<Flowing> findPagerList(Integer start, Integer length, String where, String order);
}
