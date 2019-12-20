package com.web.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.web.oa.mapper.BaoxiaobillMapper;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.BaoxiaobillExample;
import com.web.oa.service.BaoxiaoService;

@Service("baoxiaoService")
public class BaoxiaoServiceImpl implements BaoxiaoService {
	
	@Autowired
	private BaoxiaobillMapper baoxiaobillMapper;
	@Override
	public Baoxiaobill findBaoxiaoBillByBillId(String id) {
		Baoxiaobill bill = this.baoxiaobillMapper.selectByPrimaryKey(Long.parseLong(id));
		return bill;
	}
	@Override
	public List<Baoxiaobill> findBaoxiaoBillByUserId(Long id) {
		BaoxiaobillExample example = new BaoxiaobillExample();
		BaoxiaobillExample.Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(id);
		List<Baoxiaobill> list =this.baoxiaobillMapper.selectByExample(example );
		return list;
	}
	@Override
	public void deleteBaoxiaoBill(long id) {
		baoxiaobillMapper.deleteByPrimaryKey(id);
		
	}
}
