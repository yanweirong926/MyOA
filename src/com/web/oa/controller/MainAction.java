package com.web.oa.controller;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.service.EmployeeService;
import com.web.oa.utils.Constants;

@Controller
public class MainAction {
	
	//系统首页
	@RequestMapping("/main")
	public String first(Model model)throws Exception{
		
		//从shiro的session中取activeUser
		Subject subject = SecurityUtils.getSubject();
		//取身份信息
		ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
		//Employee employee =  employeeService.findSysUserByUserCode(activeUser.getUsercode());
		//通过model传到页面
		model.addAttribute("activeUser", activeUser);
		
		return "index";
	}
	
	//欢迎页面
//	@RequestMapping("/welcome")
//	public String welcome(Model model)throws Exception{
//		
//		return "welcome";
//		
//	}
}	
