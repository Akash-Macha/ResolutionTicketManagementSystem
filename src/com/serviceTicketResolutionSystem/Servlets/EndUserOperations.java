package com.serviceTicketResolutionSystem.Servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.serviceTicketResolutionSystem.DAO.EndUserDAO;
import com.serviceTicketResolutionSystem.JavaBean.Ticket;

/**
 * Servlet implementation class EndUserOperations
 */
@WebServlet("/EndUserOperations")
public class EndUserOperations extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EndUserOperations() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		/**/
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setHeader("Expires", "0"); // Proxies.		
		/**/

		response.getWriter().append("Served at: ").append(request.getContextPath());

		System.out.println("Entered into EndUserOperations.java");

		
		HttpSession session = request.getSession();
		String raised_by_user_name = (String) session.getAttribute("user_name");
		
		EndUserDAO endUserDAO = new EndUserDAO();

		Connection con = null;
		try {
			con = endUserDAO.getConnection();
			System.out.println("Connection established in EndUserOperations");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		if (request.getParameter("operation") != null) {
			System.out.println("operation parameter is not null");

			String operation = request.getParameter("operation");

			if (operation.equals("RaiseTicket")) {
				String issue_category = request.getParameter("IssueCategory");
				String message = request.getParameter("message");
				String priority = request.getParameter("priority");
				LocalDate start_date = LocalDate.parse(request.getParameter("start_date"));
				LocalDate requested_end_date = LocalDate.parse(request.getParameter("requested_end_date"));

				/*
				 * would like to create a TicketBean class and pass the bean object
				 */

				Ticket ticket = new Ticket();

				ticket.setIssue_category(issue_category);
				ticket.setMessage(message);
				ticket.setPriority(Integer.parseInt(priority));
				ticket.setStart_date(start_date);
				ticket.setRequested_end_date(requested_end_date);

				ticket.setRaised_by_user_name(raised_by_user_name);

				// generateTicket should set "end_user_id" in ticket by getting it from login
				// using user_name
				try {
					if (endUserDAO.generateTicket(con, ticket)) {

						System.out.println("inside if of generateTicket, ticket has been generated and added");
						

						/* printing the details in Ticket bean */
						System.out.println(	ticket.getEnd_user_id() + "\n" + 
											ticket.getIssue_category() + "\n" +
											ticket.getMessage() + "\n" + 
											ticket.getPriority() + "\n" +
											ticket.getRaised_by_user_name() + "\n" + 
											ticket.getService_engineer_id() + "\n" +
											ticket.getStatus() + "\n" + 
											ticket.getTicket_id() + "\n\n");

						/* Assign the ticket to  UnAssigned service_engineer */
						if (endUserDAO.checkAndAssignTicketForUnAssignedServiceEmployee(con, ticket)) {

							System.out.println("case 1 : Assigned Ticket For UnAssigned ServiceEmployee! ");

							response.sendRedirect("EndUser.jsp?operation=TicketGenerated");
							
						}
						/*
						 * 	If there are no service engineers available without tickets, 
							the method should look for those that have LOWER priority tickets and 
							assign to the one who is working on the ""MOST RECENTLY CREATED LOW PRIORITY TICKET"" [ order by DESC ] we'll get latest day
							At this time, the STATUS of the LOW PRIORITY TICKET has to be changed to PENDING
						 */
						
						else if ( (ticket.getPriority() == 10 && // new ticket is ""HIGH"" priority : Checking for ""Low"" Priority ServiceEngineer
								endUserDAO.checkForLowPriorityTicketServiceEmployee(con, ticket) ) ||
								
								(ticket.getPriority() == 5 && // new ticket is ""Medium"" priority : Checking for ""Low"" Priority ServiceEngineer
								endUserDAO.checkForLowPriorityTicketServiceEmployee(con, ticket) ) ) {	// WHERE ticket.issue_category = service_engineer.area_of_expertise
																										// and ticket.priority = 1 -- LOW
																										// ORDER BY service_engineer.current_high_prority_ticket_id desc;
							
							System.out.println("\new Ticket priority = " + ticket.getPriority() + "\n");
							
							System.out.println("There are NO UnAssigned ServiceEmployees!\n"
												+ "Thus we are in case 2 :");
							
							System.out.println("case 2 : Assigned Ticket For Medium Priority Ticket ServiceEmployee! ");

							response.sendRedirect("EndUser.jsp?operation=TicketGenerated");
							
						}else if(ticket.getPriority() == 10 && // new ticket is ""HIGH"" priority : Checking for ""Medium"" Priority ServiceEngineer
								endUserDAO.checkForMediumPriorityTicketServiceEmployee(con, ticket)) {  // WHERE ticket.issue_category = service_engineer.area_of_expertise
							
							
							System.out.println("There are NO UnAssigned ServiceEmployees! and\n"
									+	"No Low Priority Ticket ServiceEmployee\n"
									+	"Thus we are in case 3: \n");
					
							response.sendRedirect("EndUser.jsp?operation=TicketGenerated");
						}

						else {
							// put the current ticket in pending!
							System.out.println("Ticket isn't assigned to any service employee");


							response.sendRedirect("EndUser.jsp?operation=TicketGenerated&alert=TicketInPendingMode?TicketId="+ ticket.getTicket_id());
						}

						// response.sendRedirect("EndUser.jsp?operation=Error");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else if (operation.equals("ShowAllTickets")) {
				System.out.println("Entered into EndUserOperations-ShowAllTickets");
				/*
				 * Get all the records in ticket table from DAO class in an ArrayList<Ticket> : make a method ( getAllTicketsInAnArrayList ) to
				 * accomplish this task
				 */
				/* take all the records in a ArrayList and set it in the session */
				/* retreive them in the jsp page */
				
				
				 ArrayList<Ticket> listOfTickets = null;
				try {
					
					listOfTickets = endUserDAO.getAllTicketsInAnArrayList(con, raised_by_user_name);
					
					System.out.println("printing listOfTickets at line 144: " + listOfTickets);

					 /* if there are more then 1 ticket */
					 if(listOfTickets.size() >= 1){
						
						/* all the tickets of this particular user has been set in the session
						 * using an ArrayList 
						 * 
						 * Redirect to the EndUser.jsp with operation = ShowAllTickets
						 * 
						 * and display the Ticket records in the ArrayList object */
						
						
						 /* setting  */
						session.setAttribute("listOfTickets", listOfTickets);
						System.out.println("listOfTickets has been added into session");
						
						
						
						/* Redirecting to EndUser.jsp page to display All the Tickets */
						response.sendRedirect("EndUser.jsp?operation=ShowAllTickets");
						
					}
				} catch (SQLException | NullPointerException e) {
					e.printStackTrace();
				}
				
				/* display ERROR */
				if(listOfTickets == null) {
					
					
					System.out.println("Shoud redirect to an ERROR page: displaying listOfTickets( ArrayList<Ticket> ) object is null");
				}

			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		

		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setHeader("Expires", "0"); // Proxies.

		
		doGet(request, response);
	}

}
