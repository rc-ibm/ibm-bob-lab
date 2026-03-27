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
import com.ibm.security.appscan.altoromutual.util.ServletUtil;

import java.io.IOException;

/**
 * This class ensures that the user is authenticated when they try to access
 * account section of the site, and redirects to the login page if they are not.
 * @author Alexei
 *
 */
public class AuthFilter implements Filter {
	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public AuthFilter() {
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
			HttpServletRequest request = (HttpServletRequest)req;
			Object user = request.getSession().getAttribute(ServletUtil.SESSION_ATTR_USER);
			if (user == null || !(user instanceof User)){
				((HttpServletResponse)resp).sendRedirect(request.getContextPath()+"/login.jsp");
				return;
			}
			else {
				chain.doFilter(req, resp);
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