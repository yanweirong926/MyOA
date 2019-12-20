package com.web.oa.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.web.oa.mapper.EmployeeMapper;


import com.web.oa.mapper.SysRoleMapper;



import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeExample;
import com.web.oa.pojo.SysUserRole;

import com.web.oa.pojo.User;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.RoleAndPermissionService;





@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private SysRoleMapper sysRoleMapper;
	
	@Autowired
	private RoleAndPermissionService roleAndPermissionService;

	//根据用户账号查询用户信息
	@Override
	public Employee findSysUserByUserCode(String userCode) throws Exception {
		EmployeeExample employeeExample = new EmployeeExample();
		EmployeeExample.Criteria criteria = employeeExample.createCriteria();
		criteria.andNameEqualTo(userCode);
		
		List<Employee> list = employeeMapper.selectByExample(employeeExample);
		
		if(list!=null && list.size()==1){
			return list.get(0);
		}	
		
		return null;
	}
	
	
	@Override
	public Employee findEmployeeByid(Long id) {
		// TODO Auto-generated method stub
		return employeeMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<User> findUserList() {
		List<User> userList = new ArrayList<User>();
		
		List<Employee> empList = employeeMapper.selectByExample(new EmployeeExample());
		for (Employee employee : empList) {
			User user = new User();
			user.setId(employee.getId());
			user.setName(employee.getName());
			user.setEmail(employee.getEmail());
			String manager=this.findEmployeeByid(employee.getManagerId()).getName();
			user.setManager(manager);
			SysUserRole sysUserRole = this.roleAndPermissionService.findSysUserByName(employee.getName());
			String roleId =null;
			String rolename = null;
			if(sysUserRole!=null) {
				roleId = sysUserRole.getSysRoleId();
				rolename = sysRoleMapper.selectByPrimaryKey(roleId).getName();
			}
			user.setRolename(rolename);
			userList.add(user);
		}
		return userList;
	}
	
	
	@Override
	public List<Employee> findManagerListByUserRole(Integer id) {
		EmployeeExample example = new EmployeeExample();
		EmployeeExample.Criteria criteria = example.createCriteria();
		criteria.andRoleEqualTo(id);
		List<Employee> managerList =this.employeeMapper.selectByExample(example);
		
		
		return managerList;
	}
	
	@Override
	public Boolean updateEmpByUserName(String roleId, String name) {
		
		EmployeeExample example=new EmployeeExample();
		EmployeeExample.Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(name);
		Employee record = new Employee();
		record.setRole(Integer.parseInt(roleId));
		int i =this.employeeMapper.updateByExampleSelective(record , example);
		return i>0;
	}
	@Override
	public boolean saveEmployee(Employee employee) {
		employee.setSalt("eteokues");
		Md5Hash md5 = new Md5Hash(employee.getPassword(), employee.getSalt(), 2);
		employee.setPassword(md5.toString());
		this.roleAndPermissionService.saveUserSysRole(employee.getRole().toString(),employee.getName());
		return employeeMapper.insert(employee)>0;
		
	}


	@Override
	public void deleteEmployeeByUserId(Long userId) {
		this.employeeMapper.deleteByPrimaryKey(userId);
		
	}
	
	
	
	

}
