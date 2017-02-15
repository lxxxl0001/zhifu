package com.hello.zhifu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hello.zhifu.model.Award;

public interface AwardMapper {

	public Award current();
	
	public Award selectByPrimaryKey(Long termNum);
	
	public Integer insert(Award award);
	
	public Integer update(Award award);
	
	public List<Award> findList(@Param("where")String where, @Param("order")String order);
}
