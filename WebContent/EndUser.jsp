<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.serviceTicketResolutionSystem.JavaBean.Ticket" %>  

<!DOCTYPE html>
<html>
<head>
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
/*-- END: styling for Button-- */

</style>

<script type="text/javascript">
function validateDate(){
    var startDate = document.getElementById("StartDate").value;
    var endDate = document.getElementById("EndDate").value;

    if ((Date.parse(endDate) <= Date.parse(startDate))) {
        alert("End date should be greater than Start date");
        document.getElementById("EndDate").value = "";
    }
    // console.log("called");
}
</script>

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

<!-- START : IF ticket is in pending mode - alert -->

<%
	if(request.getParameter("alert") != null){
		String alert = request.getParameter("alert");
		if(alert.equals("TicketInPendingMode")){
%>

<%
		}
	}
%>
<!-- END : IF ticket is in pending mode - alert -->



<%
// not displaying the options when user chooses an operation
// if(request.getParameter("operation") == null){
%>
<center>
<h1>Welcome <%= session.getAttribute("user_name") %></h1>


<a href="EndUserOperations?operation=ShowAllTickets">Show All Tickets</a> | 
<a href="EndUser.jsp?operation=RaiseTicket">Raise A Ticket</a><br><br>

<form action="Logout" method="POST">
	<input type="submit" class="button button2" value="Logout">
</form>
 
 
<% 
// }
%>
 
<!-- Raise Ticket Form -->
<%

if(request.getParameter("operation") != null){
	String operation = request.getParameter("operation");
	if(operation.equals("RaiseTicket")){
%>

<h3>Raise Ticket:</h3><br>

<form action="EndUserOperations?operation=RaiseTicket" method="POST">

Issue Category: 
<select name="IssueCategory">
  <option value="ICT">ICT</option>
  <option value="Facility_Management">Facility_Management</option>
  <option value="Travel_And_Hospitality">Travel And Hospitality</option>
</select><br>

<div>
Message:
<textarea name="message" placeholder="Type your message here" rows="4" cols="30"></textarea><br>
</div>

Priority: 
<select name="priority">
  <option value="1">Low</option>
  <option value="5">Medium</option>
  <option value="10">High</option>
</select><br>

Start Date: <input type="date" value="<%= LocalDate.now() %>" name="start_date" id="StartDate" readonly ><br>
Requested End Date: <input type="date" value="<%= LocalDate.now() %>" name="requested_end_date" id="EndDate" onchange="validateDate();" ><br>

<input type="submit"><br>

</form>
<%
	}
%>

<!--  START: Show All Tickets -->

<%
	if(operation.equals("ShowAllTickets")){
		
%>

<!-- Ticket Id	Issue Category	start_date	requested_end_date	service_engineer_id		priority	status -->

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
  </tr>


<%
		ArrayList<Ticket> listOfTickets = (ArrayList<Ticket>)session.getAttribute("listOfTickets");
		
		if(listOfTickets == null)
			System.out.println("NULLLLLLLL");
		
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
  </tr>

<%
		}
%>

</table>

<a href="EndUser.jsp">Home</a>
<%
	}
%>

<!--  END: Show All Tickets -->


<!-- START: Ticket Submitted -->

<%
	if(operation.equals("TicketGenerated")){
%>

<h2>Ticket has been successfully raised</h2><br>
<h2>You can view the status of the Ticket in the <b>All Tickets<br>section.</h2><br><br>
<a href="EndUser.jsp">Home</a>

<%
	}
%>
<!-- END: Ticket Submitted -->


<%
} // initial null conditino if brace
%>

</center>
</body>
</html>