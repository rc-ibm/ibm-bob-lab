<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
*/
%> 
    
<jsp:include page="header.jspf"/>

<div id="wrapper" style="width: 99%;">
	<jsp:include page="toc.jspf"/>
    <td valign="top" colspan="3" class="bb">
		<%@page import="com.ibm.security.appscan.altoromutual.util.ServletUtil"%>
	
		<%
		String query = request.getParameter("query");
		String[] results = null;
		if (query != null && query.trim().length()>0)
			results = ServletUtil.searchSite(query, request.getSession().getServletContext().getRealPath("/static/"));
		%>
		
		<div class="fl" style="width: 99%;">
		
		<h1>Search Results</h1>
		
		<p>No results were found for the query:<br /><br />
		
		<%= query %>
		
		</div>    
    </td>	
</div>

<jsp:include page="footer.jspf"/>   