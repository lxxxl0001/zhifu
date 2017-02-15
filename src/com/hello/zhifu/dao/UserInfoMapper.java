package com.hello.zhifu.dao;

import com.hello.zhifu.model.UserInfo;

public interface UserInfoMapper {

	public UserInfo selectByPrimaryKey(Integer userid);
	
	public UserInfo selectByOpendId(String openid);
	
	public Integer insert(UserInfo userInfo);
	
	public Integer update(UserInfo userInfo);
}
