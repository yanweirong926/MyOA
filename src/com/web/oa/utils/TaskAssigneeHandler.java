package com.web.oa.utils;

import javax.servlet.http.HttpSession;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.service.EmployeeService;



public class TaskAssigneeHandler implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		
		ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
		//1.获取spring容器
		WebApplicationContext context= ContextLoader.getCurrentWebApplicationContext();
		EmployeeService employeeService = (EmployeeService) context.getBean("employeeService");
		Employee manager = employeeService.findEmployeeByid(activeUser.getManagerid());
		
		System.out.println(manager.getName());
		delegateTask.setAssignee(manager.getName());
	}

}
