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

/**
 * This servlet allows the user to subscribe for the mailing list
 * Servlet implementation class SubscribeServlet
 * 
 * @author Alexei
 */
public class SubscribeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String LEGAL_EMAIL_ADDRESS = "^[\\w\\d\\.\\%-]+@[\\w\\d\\.\\%-]+\\.\\w{2,4}$";
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("txtEmail");
		if (email == null || !email.matches(LEGAL_EMAIL_ADDRESS)){
			response.sendRedirect("index.jsp");
			return;
		}
		
		//don't actually subscribe the user
		request.setAttribute("message_subscribe", "Thank you. Your email " + email + " has been accepted.");
		RequestDispatcher dispatcher = request.getRequestDispatcher("subscribe.jsp");
		dispatcher.forward(request, response);
	}

}
