package com.web.oa.service;

import java.util.List;

import com.web.oa.pojo.Baoxiaobill;

public interface BaoxiaoService {
	public Baoxiaobill findBaoxiaoBillByBillId(String id);
	
	public List<Baoxiaobill> findBaoxiaoBillByUserId(Long id);

	public void deleteBaoxiaoBill(long id);
}
