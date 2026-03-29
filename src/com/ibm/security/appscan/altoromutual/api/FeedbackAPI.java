package com.ibm.security.appscan.altoromutual.api;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.wink.json4j.*;

import com.ibm.security.appscan.altoromutual.util.OperationsUtil;
import com.ibm.security.appscan.altoromutual.util.ServletUtil;
import com.ibm.security.appscan.altoromutual.model.Feedback;

@Path("/feedback")
public class FeedbackAPI extends AltoroAPI{
	
	@POST
	@PermitAll
	@Path("/submit")
	public Response sendFeedback(String bodyJSON, @Context HttpServletRequest request) throws JSONException{
		//Retrieve properties file
		//ServletUtil.initializeAppProperties(request.getServletContext());
		
		String response="";
		JSONObject myJson = new JSONObject();
		try{
		myJson =new JSONObject(bodyJSON);
		}catch (Exception e){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{Error: Request is not in JSON format}").build();
		}
		
		//Get the feedback details
		String name;
		String email;
		String subject;
		String comments;
		
		try{
			name = (String) myJson.get("name");
			email = (String) myJson.get("email");
			subject = (String) myJson.get("subject");
			comments = (String) myJson.get("message");
		}catch(JSONException e){
			return Response.status(Response.Status.BAD_REQUEST).entity("{\"Error\": \"Body does not contain all the correct attributes\"}").build();
		}
		


		String feedbackId = OperationsUtil.sendFeedback(name, email, subject, comments);
		
		if(feedbackId!=null){
			response="{\"status\":\"Thank you!\",\"feedbackId\":\""+feedbackId+"\"}";
			try{
				myJson = new JSONObject(response);
				return Response.status(Response.Status.OK).entity(myJson.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
			}
			catch(JSONException e){
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Error\":\"Unknown internal error:" + e.getLocalizedMessage() + "\"}").build();
			}
		}
		else{
			myJson = new JSONObject();
			myJson.put("name", name);
			myJson.put("email",email);
			myJson.put("subject",subject);
			myJson.put("comments",comments);
			return Response.status(Response.Status.OK).entity(myJson.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
		}
		
		//return Response.status(200).entity("bodyJSON").build();
	}
	
	@GET
	@Path("/{feedbackId}")
	public Response getFeedback(@PathParam("feedbackId") String feedbackId, @Context HttpServletRequest request){		
		Feedback feedbackDetails = ServletUtil.getFeedback(Long.parseLong(feedbackId));
		String response="";
		response+="{\"name\":\""+feedbackDetails.getName()+"\","+
				   "\n\"email\":\""+feedbackDetails.getEmail()+"\","+
				   "\n\"subject\":\""+feedbackDetails.getSubject()+"\","+
				   "\n\"message\":\""+feedbackDetails.getMessage()+"\"}";
		
		return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON_TYPE).build();
		
	}
	
}
