package com.web.oa.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.oa.exception.CustomException;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;
import com.web.oa.pojo.User;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.RoleAndPermissionService;
import com.web.oa.utils.Constants;
import com.web.oa.utils.MD5;



@Controller
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private RoleAndPermissionService roleAndPermissionService;
	
//	@RequestMapping("/login")
//	public String login(String username,String password,HttpSession session,Model model) {
//		Employee loginEmp= employeeService.findEmployeeByUserName(username);
//		
//		if(loginEmp!=null) {
//			if(loginEmp.getPassword().equals(password)) {
//				session.setAttribute(Constants.GLOBAL_SESSION_ID, loginEmp);
//				return "index";
//			}else {
//				model.addAttribute("errorMsg", "账号或密码错误");
//				return "login";
//			}
//		}else {
//			model.addAttribute("errorMsg", "账号或密码错误");
//			return "login";
//		}
//		
//		
//	}
	@RequestMapping("/login")
	public String login(HttpServletRequest request,Model model)throws Exception{
		//在作用域中获取错误的信息
		String exceptionMsg= (String) request.getAttribute("shiroLoginFailure");
		if(exceptionMsg !=null) {
			if(UnknownAccountException.class.getName().equals(exceptionMsg)) {
				//习惯返回到login.jsp，并提示
				model.addAttribute("errorMsg", "账号或密码错误");
				return "login";
			}else if(IncorrectCredentialsException.class.getName().equals(exceptionMsg)) {
				model.addAttribute("errorMsg", "账号或密码错误");
				return "login";
			}else {
				model.addAttribute("errorMsg", "未知错误1");
				return "login";
			}
		}
		//model.addAttribute("errorMsg", "未知错误2");
		//重定向到商品查询页面
		return "login";
	}
	
	
	@RequestMapping("/saveUser")
	public String saveUser(String name,String password,String email,String role,String managerId) {
		Employee employee = new Employee();
		employee.setName(name);
		employee.setPassword(password);
		employee.setEmail(email);
		employee.setRole(Integer.parseInt(role));
		employee.setManagerId(Long.parseLong(managerId));
		this.employeeService.saveEmployee(employee);
			return "redirect:/findUserList";
		
		
	}
	@RequestMapping("/getNextManager")
	@ResponseBody
	public String getNextManager(String roleId) {
		Integer id = 0; 
		if(roleId.equals("4")) {
			id=3;
		}else if(roleId.equals("3")){
			id=3;
		}else {
			int i = Integer.parseInt(roleId);
			id=i+1;
		}
		List<Employee> managerList = this.employeeService.findManagerListByUserRole(id);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(managerList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	@RequestMapping("/removeEmployee")
	public String removeEmployee(Long userId) {
		
		this.roleAndPermissionService.deleteSysUserRoleByUserId(userId);
		this.employeeService.deleteEmployeeByUserId(userId);
		return "redirect:/findUserList";
	}
	
	
//	@RequestMapping("/logout")
//	public String loginout(HttpSession session,Model model) {
//		//session失效
//		session.invalidate();
//		//重定向到商品查询页面
//		return "redirect:/main.action";
//		
//		
//	}
}
