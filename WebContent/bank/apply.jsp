<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   
<%
/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
*/
%>    
    
<jsp:include page="/header.jspf"/>

<div id="wrapper" style="width: 99%;">
	<jsp:include page="membertoc.jspf"/>
    <td valign="top" colspan="3" class="bb">
		<div class="fl" style="width: 99%;">
			<h1>Altoro Mutual Gold Visa Application</h1>
			<span><p><b>No application is needed.</b>To approve your new $10000 Altoro Mutual Gold Visa<br />with an 7.9% APR simply enter your password below.</p>
			<p><span id="_ctl0__ctl0_Content_Main_message" style="color:#FF0066;font-size:12pt;font-weight:bold;">
			<%
				java.lang.String error = (String)request.getSession(true).getAttribute("loginError");
		
				if (error != null && error.trim().length() > 0){
					request.getSession().removeAttribute("loginError");
					out.print(error);
				}
			%>
			</span></p>			
			<form method="post" name="Credit" action="ccApply"><table border=0><tr><td>Password:</td><td><input type="password" name="passwd"></td></tr><tr><td></td><td><input type="submit" name="Submit" value="Submit"></td></tr></table></form></span>
		</div>
    </td>	
</div>
<jsp:include page="/footer.jspf"/>   
