package com.ibm.security.appscan.altoromutual.api;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.ibm.security.appscan.altoromutual.util.ServletUtil;

@Path("/logout")
public class LogoutAPI extends AltoroAPI{

	@GET
	@PermitAll
	public Response doLogOut(@Context HttpServletRequest request){
		
		try{
			request.getSession().removeAttribute(ServletUtil.SESSION_ATTR_USER);
			String response="{\"LoggedOut\" : \"True\"}";
			return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build();}
		catch(Exception e){
			String response = "{\"Error \": \"Unknown error encountered\"}";
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
}
