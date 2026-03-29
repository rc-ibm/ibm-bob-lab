/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
 */

package com.ibm.security.appscan.altoromutual.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SurveyServlet
 * @author Alexei
 */
public class SurveyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SurveyServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String step = (request.getParameter("step"));
		
		String content = null;
		String previousStep = null;

		if (step == null)
			step = "";
		
		if (step.equals("a")){
			content = "<h1>Question 1</h1>"+
			"<div width=\"99%\"><p>Which of the following groups includes your age?<ul>  <li><a href=\"survey_questions.jsp?step=b\">13 years or less</a></li>  <li><a href=\"survey_questions.jsp?step=b\">14-17</a></li>  <li><a href=\"survey_questions.jsp?step=b\">18-24</a></li>  <li><a href=\"survey_questions.jsp?step=b\">25-34</a></li>  <li><a href=\"survey_questions.jsp?step=b\">35-44</a></li>  <li><a href=\"survey_questions.jsp?step=b\">45-54</a></li>  <li><a href=\"survey_questions.jsp?step=b\">55-64</a></li>  <li><a href=\"survey_questions.jsp?step=b\">65-74</a></li>  <li><a href=\"survey_questions.jsp?step=b\">75+</a></li></ul></p></div>";
			previousStep="";
		}
		else if (step.equals("b")){
			content = "<h1>Question 2</h1>"+
			"<div width=\"99%\"><p>Have you bookmarked our website?<ul><li><a href=\"survey_questions.jsp?step=c\">Yes</a></li>  <li><a href=\"survey_questions.jsp?step=c\">No</a></li></ul></p></div>";
			previousStep="a";
		}
		else if (step.equals("c")){
			content = "<h1>Question 3</h1>"+
			"<div width=\"99%\"><p>Are you ... <ul><li><a href=\"survey_questions.jsp?step=d\">Male</a></li><li><a href=\"survey_questions.jsp?step=d\">Female</a></li></ul></p>";
			previousStep="b";
		}
		else if (step.equals("d")){
			content = "<h1>Question 4</h1>"+
			"<div width=\"99%\"><p>Are you impressed with our new design?<ul><li><a href=\"survey_questions.jsp?step=email\">Yes</a></li><li><a href=\"survey_questions.jsp?step=email\">No</a></li></ul></p>";
			previousStep="c";
		} 
		else if (step.equals("email")){
			content = "<h1>Thanks</h1>"+
			"<div width=\"99%\"><p>Thank you for completing our survey.  We are always working to improve our status in the eyes of our most important client: YOU.  Please enter your email below and we will notify you soon as to your winning status.  Thanks.</p><form method=\"get\" action=\"survey_questions.jsp?step=done\"><div style=\"padding-left:30px;\"><input type=\"hidden\" name=\"step\" value=\"done\"/><input type=\"text\" name=\"txtEmail\" style=\"width:200px;\" /> <input type=\"submit\" value=\"Submit\" style=\"width:100px;\" /></div></form></div>";
			previousStep="d";
		}
		else if (step.equals("done")){
			content = "<h1>Thanks</h1>"+
			"<div width=\"99%\"><p>Thanks for your entry.  We will contact you shortly at:<br /><br /> <b>" + request.getParameter("txtEmail") + "</b></p></div>";
			previousStep="email";
		}
		else {
			content = "<h1>Welcome</h1>"+
			"<div width=\"99%\"><p>If you complete this survey, you have an opportunity to win a smartphone.  Would you like to continue?<br /><ul><li /><a href=\"survey_questions.jsp?step=a\">Yes</a></li><li><a href=\"survey_questions.jsp?step=a\">No</a></li></ul></p></div>";
		}
		
		String referrer = request.getHeader("Referer");
		String allowedReferrer = request.getContextPath()+"/survey_questions.jsp" + ((previousStep != null && previousStep.trim().length() > 0)?"?step="+previousStep:"");
		if (previousStep != null && (referrer == null || 
										!referrer.endsWith(allowedReferrer) || 
										request.getSession().getAttribute("surveyStep") == null ||
										!request.getSession().getAttribute("surveyStep").equals(previousStep))){
			content = "<h1>Request Out of Order</h1>"+
			"<div width=\"99%\"><p>It appears that you attempted to skip or repeat some areas of this survey.  Please <a href=\"survey_questions.jsp\">return to the start page</a> to begin again.</p></div>";
		} else {		
			request.getSession().setAttribute("surveyStep", step);
		}
		response.setContentType("text/html");
		response.getWriter().write(content);
		response.getWriter().flush();
		
	}
}
