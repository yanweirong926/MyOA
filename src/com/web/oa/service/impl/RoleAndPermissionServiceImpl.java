package com.web.oa.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.oa.mapper.SysPermissionMapper;
import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.mapper.SysRoleMapper;
import com.web.oa.mapper.SysRolePermissionMapper;
import com.web.oa.mapper.SysUserRoleMapper;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;
import com.web.oa.pojo.SysRoleExample;
import com.web.oa.pojo.SysRolePermission;
import com.web.oa.pojo.SysRolePermissionExample;
import com.web.oa.pojo.SysUserRole;
import com.web.oa.pojo.SysUserRoleExample;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.RoleAndPermissionService;
import com.web.oa.utils.UUIDUtils;

@Service("roleAndPermissionService")
public class RoleAndPermissionServiceImpl implements RoleAndPermissionService {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SysRoleMapper sysRoleMapper;
	
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	
	@Autowired
	private SysRolePermissionMapper sysRolePermissionMapper;
	
	@Autowired
	private SysPermissionMapperCustom sysPermissionMapperCustom;
	
	@Autowired
	private SysPermissionMapper sysPermissionMapper;
	
	@Override
	public List<MenuTree> findMenuList() throws Exception {
		// TODO Auto-generated method stub
		return sysPermissionMapperCustom.findMenuList();
	}

	@Override
	public List<SysPermission> findPermissionListByUserId(String username) throws Exception {
		// TODO Auto-generated method stub
		return sysPermissionMapperCustom.findPermissionListByEmpName(username);
	}

	@Override
	public List<SysRole> findAllRoles() {
		List<SysRole> allRoles=this.sysRoleMapper.selectByExample(new SysRoleExample());
		return allRoles;
	}

	@Override
	public Boolean updateRoleByUserName(String roleId, String userName) {
		SysUserRole sysUserRole = this.findSysUserByName(userName);
		int i=0;
		if(sysUserRole!=null) {
			SysUserRole record = new SysUserRole();
			record.setSysUserId(userName);
			record.setSysRoleId(roleId);
			SysUserRoleExample example =new SysUserRoleExample();
			SysUserRoleExample.Criteria criteria = example.createCriteria();
			criteria.andSysUserIdEqualTo(userName);
			i= this.sysUserRoleMapper.updateByExampleSelective(record, example);
		}else {
			
			i= this.saveUserSysRole(roleId, userName);
		}
		
		return i>0;
	}

	@Override
	public int saveUserSysRole(String roleId, String userName) {
		SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setId(UUIDUtils.getUUID());
		sysUserRole.setSysUserId(userName);
		sysUserRole.setSysRoleId(roleId);
		return sysUserRoleMapper.insert(sysUserRole);
		
	}

	@Override
	public SysRole findRoleByUserName(String userName) {
		try {
			SysUserRole sysUserRole = this.findSysUserByName(userName);
			if(sysUserRole!=null) {
				return this.sysRoleMapper.selectByPrimaryKey(sysUserRole.getSysRoleId());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveSysRole(String name) {
		SysRole role = new SysRole();
		role.setId(UUIDUtils.getUUID());
		role.setName(name);
		role.setAvailable("1");
		sysRoleMapper.insert(role);
	}


	@Override
	public void saveSysRolePermission(String roleId, String permissionId) {
		
		
		SysRolePermission sysRolePermission = new SysRolePermission();
		sysRolePermission.setId(UUIDUtils.getUUID());
		sysRolePermission.setSysPermissionId(permissionId);
		sysRolePermission.setSysRoleId(roleId);
		
		sysRolePermissionMapper.insert(sysRolePermission);
	}

	@Override
	public SysRole findRoleByName(String name) {
		SysRoleExample example = new SysRoleExample();
		SysRoleExample.Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(name);
		SysRole sysRole= this.sysRoleMapper.selectByExample(example).get(0);
		return sysRole;
	}

	@Override
	public void saveSysPermission(SysPermission sysPermission) {
		this.sysPermissionMapper.insert(sysPermission);
		
	}

	@Override
	public List<SysPermission> findPermissionListByRoleId(String roleId) {
		
		//通过角色id查找权限id
		SysRolePermissionExample example = new SysRolePermissionExample();
		SysRolePermissionExample.Criteria criteria = example.createCriteria();
		criteria.andSysRoleIdEqualTo(roleId);
		List<SysPermission> permissions = new ArrayList<SysPermission>();
		List<SysRolePermission> sysRolePermissions = this.sysRolePermissionMapper.selectByExample(example);
		
		//通过权限id查找权限
		for (SysRolePermission sysRolePermission : sysRolePermissions) {
			SysPermission sysPermission= this.sysPermissionMapper.selectByPrimaryKey(Long.parseLong(sysRolePermission.getSysPermissionId()));
			permissions.add(sysPermission);
		}
		
		return permissions;
	}

	@Override
	public void updateRoleAndPermission(String roleId, String[] permissionIds) {
		this.deleteSysRolePermissionByRoleId(roleId);
		for (String permissionId : permissionIds) {
			this.saveSysRolePermission(roleId, permissionId);
		}
		
	}

	@Override
	public void deleteSysRolePermissionByRoleId(String roleId) {
		SysRolePermissionExample example = new SysRolePermissionExample();
		SysRolePermissionExample.Criteria criteria = example.createCriteria();
		criteria.andSysRoleIdEqualTo(roleId);
		this.sysRolePermissionMapper.deleteByExample(example);
	}

	@Override
	public void deleteSysRoleByRoleId(String roleId) {
		this.sysRoleMapper.deleteByPrimaryKey(roleId);//角色删除
		this.deleteSysRolePermissionByRoleId(roleId);//角色权限删除
		this.deleteSysUserRoleByRoleId(roleId);//角色用户删除
	}
	@Override
	public void deleteSysUserRoleByRoleId(String roleId) {
		
			SysUserRoleExample example = new SysUserRoleExample();
			SysUserRoleExample.Criteria criteria = example.createCriteria();
			criteria.andSysRoleIdEqualTo(roleId);
			this.sysUserRoleMapper.deleteByExample(example);
		
		
		
	}

	@Override
	public List<SysUserRole> findSysUserByRoleId(String roleId) {
		SysUserRoleExample example = new SysUserRoleExample();
		SysUserRoleExample.Criteria criteria = example.createCriteria();
		criteria.andSysRoleIdEqualTo(roleId);
		List<SysUserRole> sysUserRoles=this.sysUserRoleMapper.selectByExample(example );
		
		return sysUserRoles;
	}

	@Override
	public SysUserRole findSysUserByName(String name) {
		SysUserRoleExample example = new SysUserRoleExample();
		SysUserRoleExample.Criteria criteria = example.createCriteria();
		criteria.andSysUserIdEqualTo(name);
		List<SysUserRole> sysUserRoles=this.sysUserRoleMapper.selectByExample(example );
		if(sysUserRoles!=null&&sysUserRoles.size()>0) {
			return sysUserRoles.get(0);
		}else {
			return null;
		}
		
	}

	@Override
	public void deleteSysUserRoleByUserId(Long userId) {
		Employee employee = this.employeeService.findEmployeeByid(userId);
		SysUserRoleExample example = new SysUserRoleExample();
		SysUserRoleExample.Criteria criteria = example.createCriteria();
		criteria.andSysUserIdEqualTo(employee.getName());
		this.sysUserRoleMapper.deleteByExample(example);
		
	}

	@Override
	public List<MenuTree> findAllPermissionList() throws Exception {
		
		return sysPermissionMapperCustom.findMenuAndPermissionList();
	}

}
