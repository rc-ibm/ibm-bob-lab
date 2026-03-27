/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
 */
package com.ibm.security.appscan.altoromutual.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.security.appscan.altoromutual.model.User;
import com.ibm.security.appscan.altoromutual.model.User.Role;
import com.ibm.security.appscan.altoromutual.util.ServletUtil;

import java.io.IOException;

/**
 * This filter class controls access to administrator sections of the application  
 * @author Alexei
 *
 */
public class AdminFilter implements Filter {
	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public AdminFilter() {
		super();
	}

	/* (non-Java-doc)
	 * @see javax.servlet.Filter#init(FilterConfig arg0)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// do nothing
	}

	/* (non-Java-doc)
	 * @see javax.servlet.Filter#doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest){
			//someone logged in through admin-only section of the site
			HttpServletRequest request = (HttpServletRequest)req;
			Object admObj = request.getSession().getAttribute(ServletUtil.SESSION_ATTR_ADMIN_KEY);
						
			Object user = request.getSession().getAttribute(ServletUtil.SESSION_ATTR_USER);
				
			if ((user != null && ((User)user).getRole() == Role.Admin) 
						|| (admObj != null && admObj instanceof String && ((String)admObj).equals(ServletUtil.SESSION_ATTR_ADMIN_VALUE))){
				chain.doFilter(req, resp);
				return;
			} else if (request.getRequestURL().toString().endsWith("/admin/login.jsp") || request.getRequestURL().toString().endsWith("/admin/doAdminLogin")){
				chain.doFilter(req, resp);
				return;
			} else if (user == null || !(user instanceof User)){
				((HttpServletResponse)resp).sendRedirect(request.getContextPath()+"/login.jsp");
				return;
			} else {
				((HttpServletResponse)resp).sendRedirect(request.getContextPath()+"/bank/main.jsp");
				return;
			}
		}
	}

	/* (non-Java-doc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// do nothing
	}

}