package com.web.oa.pojo;

import java.util.Iterator;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * 用户身份信息，存入session 由于tomcat将session会序列化在本地硬盘上，所以使用Serializable接口
 * 
 * @author Thinkpad
 * 
 */
public class ActiveUser implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long userid;//用户id（主键）
	private String usercode;// 用户账号
	
	private Long managerid;//用户管理员id（主键）
	
	private List<MenuTree> menuTree;//菜单树
	
	

	private List<SysPermission> permissions;// 权限


	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public List<MenuTree> getMenuTree() {
		return menuTree;
	}

	public void setMenuTree(List<MenuTree> menuTree) {
		this.menuTree = menuTree;
	}
	public List<SysPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<SysPermission> permissions) {
		this.permissions = permissions;
	}

	public Long getManagerid() {
		return managerid;
	}

	public void setManagerid(Long managerid) {
		this.managerid = managerid;
	}

	
}
