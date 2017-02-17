package com.hello.zhifu.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hello.zhifu.dao.UserInfoMapper;
import com.hello.zhifu.model.UserInfo;
import com.hello.zhifu.service.IUserInfoService;

@Service
public class UserInfoServiceImpl implements IUserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Override
	public UserInfo selectByPrimaryKey(Integer userid) {
		return userInfoMapper.selectByPrimaryKey(userid);
	}
	
	@Override
	public UserInfo selectByOpendId(String openid) {
		UserInfo userInfo = null;
		if (StringUtils.isNotEmpty(openid)){
			userInfo = userInfoMapper.selectByOpendId(openid);
			if (userInfo == null) {
				userInfo = new UserInfo();
				userInfo.setOpenid(openid);
				userInfo.setMoney(0d);
				userInfo.setAgent(0d);
				userInfoMapper.insert(userInfo);
			}
		}
		return userInfo;
	}

	@Override
	public Integer insert(UserInfo userInfo) {
		return userInfoMapper.insert(userInfo);
	}

	@Override
	public Integer update(UserInfo userInfo) {
		return userInfoMapper.update(userInfo);
	}

}
