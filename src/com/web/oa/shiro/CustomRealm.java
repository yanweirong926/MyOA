package com.web.oa.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.RoleAndPermissionService;


public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private EmployeeService sysService;
	@Autowired
	private RoleAndPermissionService roleAndPermissionService;
	//认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		
		//获取用户输入的账号
		String userCode= (String) token.getPrincipal();
		
		Employee loginUser=null;
		List<MenuTree> menuTrees=null;
		try {
			loginUser = sysService.findSysUserByUserCode(userCode);
			if(loginUser==null) {
				return null;
			}
			//查询菜单的列表
			menuTrees =roleAndPermissionService.findMenuList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//封装shiro上下文的用户的对象
		ActiveUser activeUser = new ActiveUser();
		activeUser.setUserid(loginUser.getId());
		activeUser.setUsercode(loginUser.getName());
		activeUser.setManagerid(loginUser.getManagerId());
		activeUser.setMenuTree(menuTrees);
		
		String password_db=loginUser.getPassword();//密文
		String salt = loginUser.getSalt();
		
		
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(activeUser, password_db,ByteSource.Util.bytes(salt), "CustomRealm");
		return info;
	}
	
	//授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
		
		ActiveUser activeUser = (ActiveUser) principal.getPrimaryPrincipal();
		List<String> permissions = new ArrayList<>();
		try {
			List<SysPermission> permissionList= roleAndPermissionService.findPermissionListByUserId(activeUser.getUsercode());
			for (SysPermission sysPermission : permissionList) {
				permissions.add(sysPermission.getPercode());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addStringPermissions(permissions);
		return info;
	}
}
