package com.hello.zhifu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hello.zhifu.model.Flowing;

public interface FlowingMapper {

	public Flowing selectByPrimaryKey(Long flowid);
	
	public Integer insert(Flowing flowing);
	
	public Integer update(Flowing flowing);
	
	public List<Flowing> findList(@Param("where")String where, @Param("order")String order);
	
	public List<Flowing> findHaveAmount();
	
	public Integer updateIsSend(Integer userid);
	
	public Integer getCount(@Param("where")String where);
	
	public List<Flowing> findPagerList(@Param("start")Integer start, @Param("length")Integer length, @Param("where")String where, @Param("order")String order);
}
