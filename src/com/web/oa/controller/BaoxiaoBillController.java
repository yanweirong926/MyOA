package com.web.oa.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.service.BaoxiaoService;

@Controller
public class BaoxiaoBillController {
	
	@Autowired
	private BaoxiaoService baoxiaoService;
	
	//查看报销记录
	@RequestMapping("/myBaoxiaoBill")
	public ModelAndView myBaoxiaoBill(String pageNum,HttpSession session) {
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		ModelAndView mv = new ModelAndView();
		int pageNow=0;
		if(pageNum==null) {
			pageNow=1;
		}else {
			pageNow=Integer.parseInt(pageNum);
		}
		PageHelper.startPage(pageNow, 10);
		List<Baoxiaobill> billList = this.baoxiaoService.findBaoxiaoBillByUserId(activeUser.getUserid());
		PageInfo<Baoxiaobill> info = new PageInfo<Baoxiaobill>(billList);
		
		mv.addObject("info", info);
		mv.setViewName("myBaoxiaoBill");
		return mv;
	}
	//删除报销记录
	@RequestMapping("/delBaoxiaoBill")
	public String delBaoxiaoBill(long id) {
		baoxiaoService.deleteBaoxiaoBill(id);
		return "redirect:/myBaoxiaoBill";
		
	}
	
}
