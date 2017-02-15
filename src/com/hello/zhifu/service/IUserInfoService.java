package com.hello.zhifu.service;

import com.hello.zhifu.model.UserInfo;

public interface IUserInfoService {

	public UserInfo selectByPrimaryKey(Integer userid);
	
	public UserInfo selectByOpendId(String openid);
	
	public Integer insert(UserInfo userInfo);
	
	public Integer update(UserInfo userInfo);
}
