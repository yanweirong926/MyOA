package com.web.oa.junit;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml","classpath:spring/springmvc.xml"})
public class MenuTest {

	@Autowired
	private SysPermissionMapperCustom sysPermissionMapperCustom;
	
	@org.junit.Test
	public void testMenuTree() {
		try {
			List<MenuTree> menuList = sysPermissionMapperCustom.findMenuList();
			for (MenuTree menuTree : menuList) {
				System.out.println(menuTree.getId()+".名称是"+menuTree.getName());
				for (SysPermission children : menuTree.getChildren()) {
					System.out.println(children.getName());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
