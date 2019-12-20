package com.web.oa.service;

import java.util.List;

import com.web.oa.pojo.Leavebill;

public interface LeaveService {
	
	public Leavebill findLeaveBillByBillId(String id);
	
	public List<Leavebill> findLeaveBillByUserId(Long id);

}
