package com.web.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.oa.mapper.LeavebillMapper;
import com.web.oa.pojo.Leavebill;
import com.web.oa.pojo.LeavebillExample;
import com.web.oa.service.LeaveService;

@Service("leaveService")
public class LeaveServiceImpl implements LeaveService {
	@Autowired
	private LeavebillMapper leavebillMapper;
	@Override
	public Leavebill findLeaveBillByBillId(String id) {
		Leavebill bill = this.leavebillMapper.selectByPrimaryKey(Long.parseLong(id));
		return bill;
	}
	@Override
	public List<Leavebill> findLeaveBillByUserId(Long id) {
		LeavebillExample example = new LeavebillExample();
		LeavebillExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(id);
		List<Leavebill> list =this.leavebillMapper.selectByExample(example );
		return list;
	}
}
