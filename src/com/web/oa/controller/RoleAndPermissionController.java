package com.web.oa.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;
import com.web.oa.pojo.User;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.RoleAndPermissionService;

@Controller
public class RoleAndPermissionController {
	@Autowired
	private RoleAndPermissionService roleAndPermissionService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@RequestMapping("/findUserList")
	public ModelAndView findUserList(String pageNum) {
		int pageNow=0;
		if(pageNum==null) {
			pageNow=1;
		}else {
			pageNow=Integer.parseInt(pageNum);
		}
		PageHelper.startPage(pageNow, 10);
		List<User> userList = this.employeeService.findUserList();
		PageInfo<User> info = new PageInfo<User>(userList);
		List<SysRole> allRoles = this.roleAndPermissionService.findAllRoles();
		ModelAndView mv = new ModelAndView();
		mv.addObject("info", info);
		mv.addObject("allRoles", allRoles);
		mv.setViewName("userlist");
		return mv;
	}
	@RequestMapping("/assignRole")
	@ResponseBody
	public void assignRole(HttpServletResponse response,String roleId,String userName) {
		
		Boolean flag1 = this.roleAndPermissionService.updateRoleByUserName(roleId,userName);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			if(flag1) {
				out.print("更新成功！！！");
			}else {
				out.print("更新失败！！！");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@RequestMapping("/viewPermission")
	@ResponseBody
	public Map<String, Object> viewPermission(String userName) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			SysRole sysRole=this.roleAndPermissionService.findRoleByUserName(userName);
			String roleName=null;
			List<SysPermission> permissions = new ArrayList<SysPermission>();
			if(sysRole!=null) {
				roleName = sysRole.getName();
				permissions = this.roleAndPermissionService.findPermissionListByUserId(userName);
				
			}
			map.put("permissions", permissions);
			map.put("roleName", roleName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	@RequestMapping("/toAddRole")
	public ModelAndView toAddRole() {
		ModelAndView mv = new ModelAndView();
		try {
			List<MenuTree> allPermissions = this.roleAndPermissionService.findAllPermissionList();
			mv.addObject("allPermissions", allPermissions);
			mv.addObject("menuTypes", allPermissions);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mv.setViewName("rolelist");
		return mv;
		
	}
	@RequestMapping("/saveRoleAndPermissions")
	public String saveRoleAndPermissions(String name,String[] permissionIds) {
		if(name!=null&&!name.equals("")) {
			this.roleAndPermissionService.saveSysRole(name);
			String roleId = this.roleAndPermissionService.findRoleByName(name).getId();
			if(permissionIds!=null&&permissionIds.length>0) {
				for (int i = 0; i < permissionIds.length; i++) {
					String permissionId = permissionIds[i];
					this.roleAndPermissionService.saveSysRolePermission(roleId,permissionId);
					
				}
			}
		}
		return "redirect:/toAddRole";
		
	}
	@RequestMapping("/saveSubmitPermission")
	public String saveSubmitPermission(SysPermission sysPermission) {
		if(sysPermission!=null) {
			this.roleAndPermissionService.saveSysPermission(sysPermission);
		}
		return "redirect:/toAddRole";
		
	}
	@RequestMapping("/findRoles")
	public ModelAndView findRoles() {
		ModelAndView mv = new ModelAndView();
		
		try {
			List<MenuTree> allMenuAndPermissions = this.roleAndPermissionService.findAllPermissionList();
			List<SysRole> allRoles = this.roleAndPermissionService.findAllRoles();
			mv.addObject("allMenuAndPermissions", allMenuAndPermissions);
			mv.addObject("allRoles", allRoles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mv.setViewName("permissionlist");
		return mv;
	}
	@RequestMapping("/loadMyPermissions")
	@ResponseBody
	public String loadMyPermissions(String roleId) {
		List<SysPermission> permissionList = this.roleAndPermissionService.findPermissionListByRoleId(roleId);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(permissionList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/updateRoleAndPermission")
	public String updateRoleAndPermission(String roleId,String[] permissionIds) {
		this.roleAndPermissionService.updateRoleAndPermission(roleId,permissionIds);
		return "redirect:/findRoles";
		
	}
	@RequestMapping("/removeRole")
	public String removeRole(String roleId) {
		this.roleAndPermissionService.deleteSysRoleByRoleId(roleId);
		return "redirect:/findRoles";
	}
	
}
