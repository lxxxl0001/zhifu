package com.hello.zhifu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hello.zhifu.model.UserInfo;

public interface UserInfoMapper {

	public UserInfo selectByPrimaryKey(Integer userid);
	
	public UserInfo selectByOpendId(String openid);
	
	public Integer insert(UserInfo userInfo);
	
	public Integer update(UserInfo userInfo);
	
	public List<UserInfo> findList(@Param("where")String where, @Param("order")String order);
	
	public UserInfo selectAllAmount();
}
