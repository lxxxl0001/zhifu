package com.hello.zhifu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hello.zhifu.dao.FlowingMapper;
import com.hello.zhifu.model.Flowing;
import com.hello.zhifu.service.IFlowingService;

@Service
public class FlowingServiceImpl implements IFlowingService {

	@Autowired
	private FlowingMapper flowingMapper;
	
	@Override
	public Flowing selectByPrimaryKey(Long flowid) {
		return flowingMapper.selectByPrimaryKey(flowid);
	}

	@Override
	public Integer insert(Flowing flowing) {
		return flowingMapper.insert(flowing);
	}

	@Override
	public Integer update(Flowing flowing) {
		return flowingMapper.update(flowing);
	}

	@Override
	public List<Flowing> findList(String where, String order) {
		return flowingMapper.findList(where, order);
	}

	@Override
	public List<Flowing> findHaveAmount() {
		return flowingMapper.findHaveAmount();
	}

	@Override
	public Integer updateIsSend(Integer userid) {
		return flowingMapper.updateIsSend(userid);
	}

	@Override
	public Integer getCount(String where) {
		return flowingMapper.getCount(where);
	}

	@Override
	public List<Flowing> findPagerList(Integer start, Integer length,
			String where, String order) {
		return flowingMapper.findPagerList(start, length, where, order);
	}
}
