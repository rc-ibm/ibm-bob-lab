/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
 */

package com.ibm.security.appscan.altoromutual.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.security.appscan.altoromutual.util.OperationsUtil;
import com.ibm.security.appscan.altoromutual.util.ServletUtil;

/**
 * This servlet allows to transfer funds between existing accounts
 * Servlet implementation class TransverServlet
 * 
 * @author Alexei
 */
public class TransferServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		if(!ServletUtil.isLoggedin(request)){
			response.sendRedirect("login.jsp");
			return ;
		}
		String accountIdString = request.getParameter("fromAccount");
		long creditActId = Long.parseLong(request.getParameter("toAccount"));
		double amount = Double.valueOf(request.getParameter("transferAmount"));
		
		
		String message = OperationsUtil.doServletTransfer(request,creditActId,accountIdString,amount);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("transfer.jsp");
		request.setAttribute("message", message);
		dispatcher.forward(request, response);	
	}

}
