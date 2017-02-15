package com.hello.zhifu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hello.zhifu.dao.AwardMapper;
import com.hello.zhifu.model.Award;
import com.hello.zhifu.service.IAwardService;

@Service
public class AwardServiceImpl implements IAwardService {
	
	@Autowired
	private AwardMapper awardMapper;
	
	@Override
	public Award current() {
		return awardMapper.current();
	}

	@Override
	public Award selectByPrimaryKey(Long termNum) {
		return awardMapper.selectByPrimaryKey(termNum);
	}

	@Override
	public Integer insert(Award award) {
		return awardMapper.insert(award);
	}

	@Override
	public Integer update(Award award) {
		return awardMapper.update(award);
	}

	@Override
	public List<Award> findList(String where, String order) {
		return awardMapper.findList(where, order);
	}

}
