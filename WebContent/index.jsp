<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="com.ibm.security.appscan.altoromutual.util.ServletUtil"%>
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
		<%		
		java.lang.String content = request.getParameter("content");
		if (content == null)
			content = "default.htm";
		else
			content = request.getParameter("content");
		
		if (ServletUtil.isAppPropertyTrue("advancedStaticPageProcessing")){
			String path  = request.getSession().getServletContext().getRealPath("/static");

	        %>

	        <%  try { %>
	                <%
	                 String text = "";
	                 String command = "";
					 String shell = "";
					 String shellarg = "";
					 
	                 if(System.getProperty("os.name").toLowerCase().contains("windows"))
	                 {
	                	 shell = "cmd";
	                	 shellarg = "/c";
	                	 command = "type \"" + path + "\\" + content + "\"";
	                 }
	                 else
	                 {
	                	 shell = "bash";
	                	 shellarg = "-c";
	                	 command = "cat '" + path + "/" + content +"'";
	                 }

	                 Process proc = Runtime.getRuntime().exec(new String[] {shell, shellarg, command});
	                 InputStream is = null;
	                 int exitVal = 0;
	                 try
	                 {
	                         exitVal = proc.exitValue();
	                 }
	                 catch(Exception e){}

	                 if(exitVal != 0)
	                 {
	                         is = proc.getErrorStream();
	                 }
	                 else
	                 {
	                         is = proc.getInputStream();
	                 }

					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {
						text += line + "\n";
					}

				%>
				<%=text %>
			<%  } catch (Exception e) { %>
				<p>Failed due to <%= ServletUtil.sanitzieHtmlWithRegex(e.getLocalizedMessage()) %></p>
	 		<% } %>
		<% 
		} else {
			content = "static/"+content;
		%>
		
		<%  try { %>
			<jsp:include page="<%= content %>"/>
		<%  } catch (Exception e) { %>
			<p>Failed due to <%= ServletUtil.sanitzieHtmlWithRegex(e.getLocalizedMessage()) %></p>
 		<% } 
		}%>
    </td>
	
</div>

<jsp:include page="footer.jspf"/>