package com.web.oa.service;

import java.util.List;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;
import com.web.oa.pojo.SysUserRole;
import com.web.oa.pojo.User;

public interface RoleAndPermissionService {
	
	//菜单树
	public List<MenuTree> findMenuList()throws Exception;
			
	//根据用户id查询权限范围的url
	public List<SysPermission> findPermissionListByUserId(String userCode) throws Exception;

	public List<SysRole> findAllRoles();

	public Boolean updateRoleByUserName(String roleId, String userName);

	public int saveUserSysRole(String roleId, String userName);	
	
	public SysRole findRoleByUserName(String userName);

	public void saveSysRole(String name);

	public void saveSysRolePermission(String roleId, String permissionId);
	
	public SysRole findRoleByName(String name);

	public void saveSysPermission(SysPermission sysPermission);

	public List<SysPermission> findPermissionListByRoleId(String roleId);

	public void updateRoleAndPermission(String roleId, String[] permissionIds);
	
	public void deleteSysRolePermissionByRoleId(String roleId);

	public void deleteSysRoleByRoleId(String roleId);
	
	public List<SysUserRole> findSysUserByRoleId(String roleId);
	
	public SysUserRole findSysUserByName(String name);

	public void deleteSysUserRoleByRoleId(String roleId);

	public void deleteSysUserRoleByUserId(Long userId);

	public List<MenuTree> findAllPermissionList() throws Exception;
}
