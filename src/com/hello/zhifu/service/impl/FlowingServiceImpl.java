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
	public Map<String, Object> getNumberMap(Long termNum) {
		List<Map<String, Object>> list = flowingMapper.getNumberMap(termNum);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (Map<String, Object> map : list) {
			String mkey = null;
			Object mvalue = null;
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if("carNum".equals(entry.getKey())){
					mkey = entry.getValue().toString();
				}
				if("buyAmount".equals(entry.getKey())){
					mvalue = entry.getValue().toString();
				}
			}
			resultMap.put(mkey,mvalue);
		}
		return resultMap;
	}

}
