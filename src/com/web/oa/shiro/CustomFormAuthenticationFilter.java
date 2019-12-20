package com.web.oa.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {
	
	//@Override
	//protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		
		/*
		 * HttpServletRequest req = (HttpServletRequest) request; HttpServletResponse
		 * resp = (HttpServletResponse) response; //用户输入的验证码 String randomcode =
		 * req.getParameter("randomcode"); //图片中的随机数 String validateCode = (String)
		 * req.getSession().getAttribute("validateCode"); //如果验证码不通过，return
		 * true;-->login.action;提示报错
		 * if(randomcode!=null&&validateCode!=null&&!randomcode.equals(validateCode)) {
		 * req.setAttribute(DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, "validateErrorException");
		 * return true; }
		 */
		//return super.onAccessDenied(request, response);//执行后面的操作--》调用realm
	//}
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		WebUtils.getAndClearSavedRequest(request);
		WebUtils.redirectToSavedRequest(request, response, "/main");
		return false;
	}
}
