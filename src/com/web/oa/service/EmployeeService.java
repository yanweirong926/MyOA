package com.web.oa.service;

import java.util.List;


import com.web.oa.pojo.Employee;

import com.web.oa.pojo.User;

public interface EmployeeService {
	
	//根据用户管理员id查询管理员信息
	public Employee findEmployeeByid(Long id);
			
	//根据用户账号查询用户信息
	public Employee findSysUserByUserCode(String userCode)throws Exception;
			
	public List<User> findUserList();
	
	public Boolean updateEmpByUserName(String roleId, String name);

	public List<Employee> findManagerListByUserRole(Integer id);

	public boolean saveEmployee(Employee employee);

	public void deleteEmployeeByUserId(Long userId);

}
