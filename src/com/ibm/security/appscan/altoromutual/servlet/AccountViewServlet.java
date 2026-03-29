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
 * This servlet allows the users to view account and transaction information.
 * Servlet implementation class AccountServlet
 * @author Alexei
 *
 */
public class AccountViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountViewServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//show account balance for a particular account
		if (request.getRequestURL().toString().endsWith("showAccount")){
			String accountName = request.getParameter("listAccounts");
			if (accountName == null){
				response.sendRedirect(request.getContextPath()+"/bank/main.jsp");
				return;
			}
//			response.sendRedirect("/bank/balance.jsp&acctId=" + accountName);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/bank/balance.jsp?acctId=" + accountName);
			dispatcher.forward(request, response);
			return;
		}
		//this shouldn't happen
		else if (request.getRequestURL().toString().endsWith("showTransactions"))
			doPost(request,response);
		else
			super.doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//show transactions within the specified date range (if any)
		if (request.getRequestURL().toString().endsWith("showTransactions")){
			String startTime = request.getParameter("startDate");
			String endTime = request.getParameter("endDate");
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("/bank/transaction.jsp?" + ((startTime!=null)?"&startTime="+startTime:"") + ((endTime!=null)?"&endTime="+endTime:""));
			dispatcher.forward(request, response);
		}
	}
}
