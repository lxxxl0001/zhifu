package com.hello.zhifu.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hello.zhifu.model.Flowing;

public interface FlowingMapper {

	public Flowing selectByPrimaryKey(Long flowid);
	
	public Integer insert(Flowing flowing);
	
	public Integer update(Flowing flowing);
	
	public List<Flowing> findList(@Param("where")String where, @Param("order")String order);
	
	public Map<Integer, Integer> getNumberMap(Long termNum);
}
