package com.web.oa.mapper;

import java.util.List;

import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;



public interface SysPermissionMapperCustom {
	
	//根据用户id查询权限url
	public List<SysPermission> findPermissionListByEmpName(String name)throws Exception;
	//查询主菜单
	public List<MenuTree> findMenuList()throws Exception;
	//查询子菜单
	public List<SysPermission> getSubMenus()throws Exception;
	//查询所有权限
	public List<MenuTree> findMenuAndPermissionList()throws Exception;
	//查询子菜单
	public List<SysPermission> getSubPermission()throws Exception;
}
