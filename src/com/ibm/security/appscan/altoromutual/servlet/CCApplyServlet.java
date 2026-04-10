/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
 */
package com.ibm.security.appscan.altoromutual.servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ibm.security.appscan.altoromutual.model.User;
import com.ibm.security.appscan.altoromutual.util.DBUtil;
import com.ibm.security.appscan.altoromutual.util.ServletUtil;

/**
 * Credit card application servlet
 * @author Alexei
 */
public class CCApplyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//do nothing with credit card application
		//redirect to success page
		
		try {
			String passwd = request.getParameter("passwd");
			User user = (User)(request.getSession().getAttribute(ServletUtil.SESSION_ATTR_USER));
			
			//correct password entered
			if (DBUtil.isValidUser(user.getUsername(), passwd.trim().toLowerCase())) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/bank/applysuccess.jsp");
				dispatcher.forward(request, response);	
			}
			//incorrect password entered
			else{
				request.getSession().setAttribute("loginError", "Login Failed: We're sorry, but this username or password was not found in our system. Please try again.");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/bank/apply.jsp");
				dispatcher.forward(request, response);
			}
			
		} catch (Exception e) {
			response.sendError(500);
		}
				

	}

}
