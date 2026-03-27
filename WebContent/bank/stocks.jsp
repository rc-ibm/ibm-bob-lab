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
			<div style="display:inline;">
				<script type="text/javascript" src="../util/swfobject.js"></script>
				<script type="text/javascript">
						swfobject.registerObject("myId", "9.0.0", "../util/expressInstall.swf");
				</script>
			
				<object id="myId" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="550" height="750">
					<param name="movie" value="../util/EasyStock.swf" />
			   		<!--[if !IE]>-->
					<object type="application/x-shockwave-flash" data="../util/EasyStock.swf" width="550" height="750">
					<!--<![endif]-->
			</div>
		</div>
    </td>	
</div>
<jsp:include page="/footer.jspf"/>   

<div style="display:none;">
	<script type="text/javascript" src="util/swfobject.js"></script>
	<script type="text/javascript">
			swfobject.registerObject("myId", "9.0.0", "util/expressInstall.swf");
	</script>

	<object id="myId" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="400" height="300">
		<param name="movie" value="util/vulnerable.swf" />
   		<!--[if !IE]>-->
		<object type="application/x-shockwave-flash" data="util/vulnerable.swf" width="400" height="300">
		<!--<![endif]-->
</div>