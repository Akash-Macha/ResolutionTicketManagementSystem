package com.serviceTicketResolutionSystem.Servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.serviceTicketResolutionSystem.DAO.ServiceEngineerDAO;
import com.serviceTicketResolutionSystem.JavaBean.Ticket;

/**
 * Servlet implementation class ServiceEngineerOperations
 */
@WebServlet("/ServiceEngineerOperations")
public class ServiceEngineerOperations extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceEngineerOperations() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		System.out.println("Entered into ServiceEngineerOperations.java.java");
		
		ServiceEngineerDAO serviceEngineerDAO = new ServiceEngineerDAO();
		
		HttpSession session = request.getSession();
		String user_name = (String) session.getAttribute("user_name");
		
		
		/* connection  */
		Connection con = null;
		try {
			con = serviceEngineerDAO.getConnection();
			System.out.println("Conenction established in ServiceEngineerOperations");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String operation = request.getParameter("operation");
		if(operation != null) {
			
			
			ArrayList<Ticket> listOfTickets = null;
			if(operation.equals("Show_All_Tickets")) {
				
				try {
					listOfTickets = serviceEngineerDAO.getAllTicketsInAnArrayList(con, user_name);
					
					System.out.println("printing listOfTickets in ServiceEngineerOperations: " + listOfTickets);


	                /* all the tickets of this particular user has been set in the session
	                 * using an ArrayList 
	                 * 
	                 * Redirect to the ServiceEngineer.jsp with operation = ShowAllTickets
	                 * 
	                 * and display the Ticket records in the ArrayList object */
	                
	                
	                 /* setting listOfTickets into session */
	                session.setAttribute("listOfTickets", listOfTickets);
	                System.out.println("listOfTickets has been added into session");
	                
	                
	                
	                /* Redirecting to ServiceEngineer.jsp page to display All the Tickets */
	                response.sendRedirect("ServiceEngineer.jsp?operation=ShowAllTickets");			
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(operation.equals("CloseTicket")) {
				System.out.println("Entered into ServiceEngineerOperations -> CloseTicket");

				// 1
				Integer service_engineer_id = Integer.parseInt( request.getParameter("ServiceEngineerId") );
				System.out.println("service_engineer_id = " + service_engineer_id);
				
				if(serviceEngineerDAO.closeTicket(con, service_engineer_id)) {
					
					/* ticket has been closed successfully */
					
					
					/* ************************* */
					/* check whether there are any pending tickets in the current ServiceEngineer 
					 * area_of_expertise category
					 * 
					 *  if found assign that ticket to the current ServiceEngineer
					 *  else
					 *  	leave it as is !
					*/	
					/* ************************* */
					
					/* searching for pending tickets  */
					/*
					 * we have service_engineer_id | use it to update service_engineer table
					 * 
					 * WHERE 
					 * 		service_engineer.area_of_expertise = ticket.issue_category
					 */
					try {
						if(serviceEngineerDAO.checkPendingTicketsIfFoundAssign(con, service_engineer_id)) {
							
							System.out.println("woah found a pending ticket and has been assigned to a "
									+ "service_engineer with service_engineer_id = "+ service_engineer_id);
							
						}else {
							System.out.println("No pending tickets found after closing the current ticket");
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					
					
					
					/* updating the listOfTickets object in the session.
					 * to display the updated listOfTickets */
					try {
						listOfTickets = serviceEngineerDAO.getAllTicketsInAnArrayList(con, user_name);
						
						System.out.println("printing listOfTickets in ServiceEngineerOperations-ClosedTicket: " + listOfTickets);
						
		                session.setAttribute("listOfTickets", listOfTickets);
		                System.out.println("listOfTickets has been UPDATED into session - ServiceEngineerOperations-ClosedTicket:");
					} catch (SQLException e) {
						e.printStackTrace();
					}

					
					/* redirecting to ServiceEngineer.jsp and displaying the closed ticket prompt */
					response.sendRedirect("ServiceEngineer.jsp?display=DispalyTicketClosedsuccessfully&operation=ShowAllTickets");
				}
			}
			/* Average_Time_Taken_Per_Engineer */
			else if(operation.equals("Average_Time_Taken_Per_Engineer")) {
				
				/* */
				ArrayList<ArrayList> averageTimeTakenPerEngineer = null;
				try {
					averageTimeTakenPerEngineer = serviceEngineerDAO.getStatsOfEngineer(con);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				
				/* setting the  averageTimeTakenPerEngineer  into session*/
				session.setAttribute("Average_Time_Taken_Per_Engineer", averageTimeTakenPerEngineer);
				
				response.sendRedirect("ServiceEngineer.jsp?operation=DisplayAverageTimeTakenPerEngineer");
				
				return;
			}
			
			else if(operation.equals("Average_Time_Taken_Per_Severity")) {
				/* select all the records form Ticket and calculate AVG time take by the severity */
				
				ArrayList<Double> averageTimeTakenPerSeverity = new ArrayList<>();
				
//				String lowPriorityStats_query = ""
//						+ "select start_date, closed_date "
//						+ "from ticket "
//						+ "where priority = 1";
				
				int[] arr = {1, 5, 10};
				
				for(int i : arr) {
					averageTimeTakenPerSeverity.add(  serviceEngineerDAO.getStatsOfPriority(con, i)  );					
				}

				System.out.println("setting averageTimeTakenPerSeverity into session");
				session.setAttribute("Avg_Time_Taken_Per_Severity", averageTimeTakenPerSeverity);
				
				response.sendRedirect("ServiceEngineer.jsp?operation=DisplayAverageTimeTakenPerSeverity");
				
				return;
			}
			else if( operation.equals("Aging_of_Open_Tickets") ) {
				
				ArrayList<ArrayList> agingOfOpenTickets = null;
				
				try {
					
					System.out.println("Calling getAgingOfOpenTicket()");
					
					agingOfOpenTickets = serviceEngineerDAO.getAgingOfOpenTicket(con);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Setting AgingOfOpenTickets into session");
				session.setAttribute("AgingOfOpenTickets", agingOfOpenTickets);
				
				response.sendRedirect("ServiceEngineer.jsp?operation=DisplayAgingOfOpenTickets");
				
				return;
			}
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
