package com.hello.zhifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public Map<Integer, Integer> getNumberMap(Long termNum) {
		List<Map<String, Integer>> list = flowingMapper.getNumberMap(termNum);
		Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		for (Map<String, Integer> map : list) {
			Integer mkey = null;
			Integer mvalue = null;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if("carNum".equals(entry.getKey())){
					mkey = entry.getValue();
				}
				if("buyAmount".equals(entry.getKey())){
					mvalue = entry.getValue();
				}
			}
			resultMap.put(mkey,mvalue);
		}
		return resultMap;
	}

}
