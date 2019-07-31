<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="java.time.LocalDate" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.serviceTicketResolutionSystem.JavaBean.Ticket" %>  

<!DOCTYPE html>
<html>
<head>

<meta charset="ISO-8859-1">

<!-- START: styling for Table [ tickets ] displaying -->
<style>
/* ----START: Table------ */
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 90%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
/*----END: Table --------*/

/*
* Basic styles
*/
body {
  margin: 0;
  font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;
  font-size: 1.15em;
  line-height: 1.5;
}

a {
  color: var(--c-interactive);
}

h1 {
  margin-top: 0;
}

code {
  font-family: 'Fira Mono', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
}

.wrapper {
  max-width: 600px;
}

/* to remove underline from achor tags */
a, u {
    text-decoration: none;;
}


/*-- START: styling for Button-- */
.button {
  background-color: #4CAF50; /* Green */
  border: none;
  color: white;
  padding: 10px 20px; /* padding: 15px 32px; */
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 2px 1px;
  cursor: pointer;
  -webkit-transition-duration: 0.4s; /* Safari */
  transition-duration: 0.4s;
}

.button2:hover {
  box-shadow: 0 12px 16px 0 rgba(0,0,0,0.24),0 17px 50px 0 rgba(0,0,0,0.19);
}
/* close button */
.close_button {
  background-color: black; /* Green */
  border: none;
  color: white;
  padding: 10px 20px; /* padding: 15px 32px; */
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 2px 1px;
  cursor: pointer;
  -webkit-transition-duration: 0.4s; /* Safari */
  transition-duration: 0.4s;
}

/*-- END: styling for Button-- */
</style>


<title>Service Ticket Resolution System</title>
</head>

<body>

<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	response.setHeader("Expires", "0"); // Proxies.
%>
<!-- START:  If person tries to Come back after loggin OUT : redirect him/her to login page -->
<%
	if(session.getAttribute("user_name") == null){
		System.out.println("session.getAttribute(user_name) == null");
		
		response.sendRedirect("index.jsp?warning=UnAuthorizedLogin");
		return;
	}
%>
<!-- END: If person tries to Come back after loggin OUT -->

<center>

<!-- START: DispalyTicketClosedsuccessfully -->
<%
if(request.getParameter("display") != null){
	String operation = request.getParameter("display");
	if(operation.equals("DispalyTicketClosedsuccessfully")){
%>
<h3>Ticket has been closed successfully!</h3>
<%
	}
}
%>
<!-- END: DispalyTicketClosedsuccessfully -->


<h1>Welcome <%= session.getAttribute("user_name") %></h1>

<a href="ServiceEngineerOperations?operation=Show_All_Tickets">Show All Tickets</a><br>


<!-- Statistics :  -->
<a href="ServiceEngineerOperations?operation=Average_Time_Taken_Per_Engineer">Average Time Taken Per Engineer</a> | 
<a href="ServiceEngineerOperations?operation=Average_Time_Taken_Per_Severity">Average Time Taken Per Severity</a><br><br>
<a href="ServiceEngineerOperations?operation=Aging_of_Open_Tickets">Aging of Open Tickets</a><br><br>

<form action="Logout" method="POST">
	<input type="submit" class="button button2" value="Logout">
</form>



<%

if(request.getParameter("operation") != null){
	String operation = request.getParameter("operation");
	
	if(operation.equals("ShowAllTickets")){
%>

<!-- START: Show All Tickets -->

<h2>All Tickets</h2>

<table>
    <!-- Table heading -->
  <tr>
    <th>Ticket Id</th>
    <th>Issue Category</th>
    
    <th>Message</th>
    
    <th>start_date</th>
    <th>requested_end_date</th>
    
    <th>service_engineer_id</th>
    <th>priority</th>
    <th>status</th>
    
    <th>Close</th>
  </tr>

<%
        ArrayList<Ticket> listOfTickets = (ArrayList<Ticket>)session.getAttribute("listOfTickets");
        
		/* this condition may not get a chance to execute, since it will be caught in the above user_name == null case itself! */
        if(listOfTickets == null){
            System.out.println("listOfTickets == NULL in ServiceEngineer.jsp -> ShowAllTickets");
            System.out.println("Redirecting to index page");
            
            response.sendRedirect("index.jsp?warning=UnAuthorizedLogin");
            
            return;
        }
        
        //for(Ticket ticket : listOfTickets){
        for(int i=0 ; i < listOfTickets.size() ; ++i){
%>

<!-- Table rows -->

    <tr>
    <td> <%= listOfTickets.get(i).getTicket_id()  %> </td>
    <td> <%= listOfTickets.get(i).getIssue_category()  %> </td>
    
    <td> <%= listOfTickets.get(i).getMessage()  %> </td>
    
    <td> <%= listOfTickets.get(i).getStart_date()  %> </td>
    <td> <%= listOfTickets.get(i).getRequested_end_date()  %> </td>
    <td> <%= listOfTickets.get(i).getService_engineer_id()  %> </td>
    <td> <%= listOfTickets.get(i).getPriority()  %> </td>
    <td> <%= listOfTickets.get(i).getStatus()  %> </td>
    
    <td> <% if(listOfTickets.get(i).getStatus().equals("on_going")){   %>
    			<form action="ServiceEngineerOperations?operation=CloseTicket&ServiceEngineerId=<%= listOfTickets.get(i).getService_engineer_id() %>" method="POST">
    				<!-- <input type="submit" value="Close Ticket">  -->
    				  <input type="submit" class="close_button" value="Close Ticket">
    			</form>
    		<% } %>
	</td>
    
  </tr>

<%
		}
%>
</table>

<%
	}
%>
<!-- END: Show All Tickets -->


<!-- START: AverageTimeTakenPerSeverity -->
<%
	if(operation.equals("DisplayAverageTimeTakenPerSeverity")){
		
		ArrayList<Double> averageTimeTakenPerSeverity = (ArrayList<Double>)session.getAttribute("Avg_Time_Taken_Per_Severity");
%>

<h2>Avergae time taken per Severity</h2>
<table>
    <!-- Table heading -->
  <tr>
    <th>Severity</th>
    <th>Average Time Taken</th>
  </tr>

<!-- Table rows -->

    <tr>
    	<td> 1 </td>
	    <td> <%= averageTimeTakenPerSeverity.get(0)  %> </td>
	</tr>

    <tr>
    	<td> 5 </td>
	    <td> <%= averageTimeTakenPerSeverity.get(1)  %> </td>
	</tr>

    <tr>
    	<td> 10</td>
	    <td> <%= averageTimeTakenPerSeverity.get(2)  %> </td>
	</tr>
</table>

<%
	}
%>
<!-- END: AverageTimeTakenPerSeverity -->

<!-- START: AverageTimeTakenPerEngineer -->
<!--  operation=DisplayAverageTimeTakenPerEngineer  -->
<%
	if(operation.equals("DisplayAverageTimeTakenPerEngineer")){
		
		ArrayList<ArrayList> averageTimeTakenPerEngineer = (ArrayList<ArrayList>)session.getAttribute("Average_Time_Taken_Per_Engineer");
		
%>
<h2>Avergae time taken per Engineer</h2>
<table>
    <!-- Table heading -->
  <tr>
    <th>Employee Name</th>
    <th>Average Time Taken</th>
  </tr>

<%
		for(ArrayList userNameAndHisStats : averageTimeTakenPerEngineer){
%>

<!-- Table rows -->
    <tr>
    	<td> <%= userNameAndHisStats.get(0)  %> </td>
	    <td> <%= userNameAndHisStats.get(1)  %> </td>
	</tr>
<%
		}
%>

</table>

<%
	}
%>
<!-- END: AverageTimeTakenPerEngineer -->


<%
// operation=DisplayAgingOfOpenTickets
	if(operation.equals("DisplayAgingOfOpenTickets")){
		ArrayList<ArrayList> agingOfOpenTickets = (ArrayList<ArrayList>) session.getAttribute("AgingOfOpenTickets");
%>

<h2>Avergae time taken per Engineer</h2>
<table>
    <!-- Table heading -->
  <tr>
    <th>Ticket Id</th>
    <th>Issue category</th>
    <th>Priority</th>
    <th>Start Date</th>
    <th>AGE</th>
    <th>Status</th>
    <th>Service Engineer Id</th>
    <th>Message</th>
  </tr>

<%
		for(ArrayList eachOpenTicket : agingOfOpenTickets){
%>

<!-- Table rows -->
    <tr>
    	<td> <%= eachOpenTicket.get(0)  %> </td>
    	<td> <%= eachOpenTicket.get(1)  %> </td>
    	<td> <%= eachOpenTicket.get(2)  %> </td>
    	<td> <%= eachOpenTicket.get(3)  %> </td>
    	<td> <%= eachOpenTicket.get(4)  %> </td>
    	<td> <%= eachOpenTicket.get(5)  %> </td>
    	<td> <%= eachOpenTicket.get(6)  %> </td>
    	<td> <%= eachOpenTicket.get(7)  %> </td>
	</tr>
<%
		}
%>

</table>

<%
	}
%>



<%
} //  ("operation") != null)
%>

</center>
</body>
</html>