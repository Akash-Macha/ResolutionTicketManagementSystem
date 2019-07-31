package com.serviceTicketResolutionSystem.DAO;

import static java.time.temporal.ChronoUnit.DAYS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.serviceTicketResolutionSystem.JavaBean.Ticket;

public class ServiceEngineerDAO {
	Connection con = null;
	
	public Connection getConnection() throws SQLException, ClassNotFoundException {

		/* create connection IF not already created */
		if(this.con == null) {		
			System.out.println("Create connection called!");
	
			String dbDriver = "com.mysql.jdbc.Driver";
			String dbURL = "jdbc:mysql://localhost:3306/";
	
			// Database name to access
			String dbName = "strs";
			String dbUsername = "root";
			String dbPassword = "root";
	
			Class.forName(dbDriver);
			con = DriverManager.getConnection(dbURL + dbName, dbUsername, dbPassword);
		}
		else {
			System.out.println("Already created Conncetion Object is Used");
		}
		System.out.println(con.toString());
		
		return con; 
	}
	
	public ArrayList<Ticket> getAllTicketsInAnArrayList(Connection con, String user_name) throws SQLException {
		
		String getAllRecordsOfThisEndUser_query = "" +
				"SELECT ticket.ticket_id, " + 
				"ticket.issue_category, " + 
				"ticket.message, " + 
				"ticket.start_date, " + 
				"ticket.requested_end_date, " + 
				"ticket.service_engineer_id, " + 
				"ticket.priority, " + 
				"ticket.status " + 
				"from login, ticket " + 
				"where login.user_id = ticket.service_engineer_id and " + 
				"login.user_name = ? ; ";
				
		PreparedStatement preparedStatement_getAllRecordsOfThisEndUser = con.prepareStatement(getAllRecordsOfThisEndUser_query);
		System.out.println("End_user_id used to retrieve data from ticket table = " + user_name);
		preparedStatement_getAllRecordsOfThisEndUser.setString(1, user_name); // raised_by_user_name == end_user_id

	    
	    ArrayList<Ticket> listOfTickets = new ArrayList<Ticket>();   /*  https://stackoverflow.com/a/10417020 */
	    
	    ResultSet listOfTickets_rs = preparedStatement_getAllRecordsOfThisEndUser.executeQuery();
	    
	    
	    System.out.println("Query has been executed");
	    
	    Ticket ticket = null;
	    
	    while ( listOfTickets_rs.next() ) {
	    	System.out.println("Line: 71 - Entered into while loop");
	    	
	    	/*
	    	 * All Tickets:	| ticket_id | issue_category	| end_user_id | priority | start_date | requested_end_date | status   | service_engineer_id |
	    	 * 
	    	 * Ticket Id	Issue Category	start_date	requested_end_date	service_engineer_id		priority	status
	    	 * 
	    	 */
	    	
	    	ticket = new Ticket();
	    	
	    	ticket.setTicket_id( listOfTickets_rs.getInt("ticket_id") );
	    	ticket.setIssue_category( listOfTickets_rs.getString("issue_category") );
	    	
	    	ticket.setMessage( listOfTickets_rs.getString("message") );
	    	
	    	ticket.setStart_date( listOfTickets_rs.getDate("start_date").toLocalDate() );
	    	ticket.setRequested_end_date( listOfTickets_rs.getDate("requested_end_date").toLocalDate() );
	    	
	    	ticket.setService_engineer_id( listOfTickets_rs.getInt("service_engineer_id") );
	    	ticket.setPriority( listOfTickets_rs.getInt("priority") );
	    	
	    	ticket.setStatus( listOfTickets_rs.getString("status") );
	    	
	    	
	    	/* adding ticket object in to ArrayList<Ticket> */
	    	listOfTickets.add(ticket);
	    	
	    }
	    
	    System.out.println("length of ArrayList = " + listOfTickets.size());
	    
		return listOfTickets;
	}
	
	public boolean closeTicket(Connection con, Integer service_engineer_id ) {
		
		/* The Approach:
		 * 	1. get the ServiceEngineerId from the URL , ie. request.getParameter("ServiceEngineerId") and parse it into Integer
		 * 
		 * 		service_engineer table:
		 * 
		 *  2. Increment the 	service_engineer.total_tickets_worked_on
		 *  
		 *  3. SET   			service_engineer.current_ticket_start_date 		= NULL
		 *  4. SET				service_engineer.current_high_prority_ticket_id = -1
		 *  5. SET				priority = null
		 *  			WHERE service_engineer_id = ?
		 *  
		 *  
		 *  	ticket table:
		 *  
		 *  5. SET				ticket.status 		= 'closed'
		 *  6. SET				ticket.closed_date 	= LocalDate.now()   !!!!!!!!!!!!!!!!!!!!!	ADD the COLUMN
		 *  
		*/
		

		// 2, 3, 4 : updating service_engineer table
//		String updating_service_engineer_table_query = ""
//				+ "UPDATE service_engineer "
//				+ "SET 	total_tickets_worked_on = total_tickets_worked_on + 1 , "
//				+ "		current_ticket_start_date = ?, "
//				+ "		current_high_prority_ticket_id = ? "
//				+ "WHERE service_engineer_id = ? ; ";
		
		String updating_service_engineer_table_query = "" +
				"UPDATE " + 
				"  service_engineer " + 
				"SET " + 
				"  total_tickets_worked_on = total_tickets_worked_on + 1, " + 
				"  current_ticket_start_date = ?, " + 
				"  current_high_prority_ticket_id = ?, " + 
				"  priority = ? " + // <------------------------------------- ADDED - set it as NULL
				"WHERE " + 
				"  service_engineer_id = ? ; ";
		
		try (
				PreparedStatement updating_service_engineer_table_ps = con.prepareStatement(updating_service_engineer_table_query);
		    ) {
				updating_service_engineer_table_ps.setDate(1, null);
				updating_service_engineer_table_ps.setInt(2, -1);
				
				updating_service_engineer_table_ps.setString(3, null); // priority
				
				updating_service_engineer_table_ps.setInt(4, service_engineer_id);
				
	            if(updating_service_engineer_table_ps.executeUpdate() >= 1) {
	            	/* SUCESSFULLY updated '''service_engineer''' table */
	            	
	            	
	            	/* updating '''ticket''' table */
	            	
	            	// 5: ticket.status 		= 'closed' 
	            	// 6: ticket.closed_date 	= LocalDate.now()
	            	
	            	String updating_ticket_table_query = "" +
	            			"UPDATE " + 
	            			"  ticket " + 
	            			"SET " + 
	            			"  status = 'closed', " + 
	            			"  closed_date = ? " + 
	            			"WHERE " + 
	            			"  service_engineer_id = ? ; ";

	            	try( 
	            			PreparedStatement updating_ticket_table_ps = con.prepareStatement(updating_ticket_table_query); 
	            		)
		            	{
		            		updating_ticket_table_ps.setDate(1, java.sql.Date.valueOf( LocalDate.now() ));  // today
		            		updating_ticket_table_ps.setInt(2, service_engineer_id);
		            		
		            		if(updating_ticket_table_ps.executeUpdate() >= 1) {
		            			
		            			/* SUCESSFULLY updated '''ticket''' table */
		            			
		            			return true;
		            		}
		            	}
	            }
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return false;
	}
	
	public boolean checkPendingTicketsIfFoundAssign(Connection con, Integer service_engineer_id) throws SQLException {
		
		/* get area_of_expertise from service_engineer table */
		String getAreaOfExpertise = ""
				+ "Select area_of_expertise "
				+ "from service_engineer "
				+ "where service_engineer_id = ? ; ";
		
		
		String area_of_expertise = null;
		
		try (
		        PreparedStatement preparedStatement_getAreaOfExpertise = con.prepareStatement(getAreaOfExpertise);
		    ) {
				preparedStatement_getAreaOfExpertise.setInt(1, service_engineer_id);

		        try (ResultSet resultSet_getAreaOfExpertise = preparedStatement_getAreaOfExpertise.executeQuery()) {
		            if (resultSet_getAreaOfExpertise.next()) {
		            	
		            	area_of_expertise = resultSet_getAreaOfExpertise.getString("area_of_expertise");
		            	
		            }else {
		            	System.out.println("UnAble to fetch area_of_expertise from service_engineer table ");
		            }
		        }
		    }
			/* got the area_of_expertise */
		
		
		String getPendingTicketId = ""
				+ "select "
				+ "	ticket_id, "
				+ "	priority " // ADDED priority, to set it in the service_employee table
				+ "from ticket "
				+ "where status = 'pending' "
				+ "and issue_category = ? "
				+ "order by priority DESC ; "; // ordering = high priority to low
		
		try (
		        PreparedStatement preparedStatement_getPendingTicketId = con.prepareStatement(getPendingTicketId);
		    ) {
				preparedStatement_getPendingTicketId.setString(1, area_of_expertise);

		        try (ResultSet resultSet_getPendingTicketId = preparedStatement_getPendingTicketId.executeQuery()) {
		            if (resultSet_getPendingTicketId.next()) {
		            	
		            	 /* Woah there is a peding ticket =
		            	  * Let's assign this ticket to the current service_engineer */
		            	
		            	Integer ticket_id = resultSet_getPendingTicketId.getInt("ticket_id");
		            	
		            	Integer priority = resultSet_getPendingTicketId.getInt("priority"); // used in updating the service_employee
		            	
		            	/* updating the ticket table */
		            	String updateTicketTable = ""
		            			+ "update ticket "
		            			+ "set 	status = 'on_going', "
		            			+ "		service_engineer_id = ? "
		            			+ "where ticket_id = ? ; ";
		            	
		            	PreparedStatement preparedStatement_updateTicketTable = con.prepareStatement(updateTicketTable);
		            	preparedStatement_updateTicketTable.setInt(1, service_engineer_id );
		            	preparedStatement_updateTicketTable.setInt(2, ticket_id );

		            	System.out.println("updating the ticket table!");
		            	
		                if(preparedStatement_updateTicketTable.executeUpdate() >= 1) {
		                    System.out.println("successfully updated the ticket table");
		                    
		                    
		                    /* After updating ticket table
		                     * updating the """service_engineer""" table  */
		                    
			            	String updateServiceEngineerTable = ""
			            			+ "update service_engineer "
			            			+ "set 	current_ticket_start_date = ?, "
			            			+ "		current_high_prority_ticket_id = ?, "
			            			+ "		priority = ? " // <---------------------  ADDED: 
			            			+ "where service_engineer_id = ? ; ";
			            	
			            	PreparedStatement preparedStatement_updateServiceEngineerTable = con.prepareStatement(updateServiceEngineerTable);
			            	preparedStatement_updateServiceEngineerTable.setDate(1, java.sql.Date.valueOf( LocalDate.now() ) );
			            	preparedStatement_updateServiceEngineerTable.setInt(2, ticket_id );
			            	
			            	preparedStatement_updateServiceEngineerTable.setInt(3, priority );
			            	
			            	preparedStatement_updateServiceEngineerTable.setInt(4, service_engineer_id );
			            	
			            	if(preparedStatement_updateServiceEngineerTable.executeUpdate() >= 1) {
			            		System.out.println("successfully updated the service_engineer table");
			            		
			            		
			            		/* !!!!! */
			            		return true;
			            		
			            	}else {
			            		System.out.println("NOT updated the service_engineer table");
			            	}
			            	
		                }else {
		                	System.out.println("NOT updated the ticket table");
		                }
		            }
		        }
		    }
		
		return false;
	}
	
	public ArrayList<ArrayList> getStatsOfEngineer(Connection con) throws SQLException{
		
		ArrayList<ArrayList> averageTimeTakenPerEngineer = new ArrayList<>();
		
		/*  user_name = stats */
		ArrayList userNameAndHisStats = null;
		
		
		/* get all service_engineer_id  's  from  service_engineer*/
		String getAllServiceEngineerId_query = ""
				+ "Select "
				+ "		service_engineer_id, "
				+ "		user_name "
				+ "from "
				+ "		service_engineer ; ";
		
		try (
		        PreparedStatement preparedStatement_getAllServiceEngineerId = con.prepareStatement(getAllServiceEngineerId_query);
		    ) {

		        try (ResultSet resultSet_getAllServiceEngineerId = preparedStatement_getAllServiceEngineerId.executeQuery()) {

		        	/* */
		        	while(resultSet_getAllServiceEngineerId.next()) { /* for each engineer */
		        		
		        		Integer service_engineer_id = resultSet_getAllServiceEngineerId.getInt("service_engineer_id");
		        		String user_name = resultSet_getAllServiceEngineerId.getString("user_name");
		        		
		        		String getDifferenceOfEachEmployee_query = ""
		        				+ "select avg(DATEDIFF(closed_date, start_date)) " 
		        				+ "from ticket " 
		        				+ "where service_engineer_id = ? ; ";

		        		try (
		        		        PreparedStatement preparedStatement_getDifferenceOfEachEmployee = con.prepareStatement(getDifferenceOfEachEmployee_query);
		        		    ) {
		        				
		        				preparedStatement_getDifferenceOfEachEmployee.setInt(1,   service_engineer_id);
		        				
		        		        try (ResultSet resultSet_getDifferenceOfEachEmployee = preparedStatement_getDifferenceOfEachEmployee.executeQuery()) {
		        		        	/* we will have only one record, ie. AVG value */
		        		            if (resultSet_getDifferenceOfEachEmployee.next()) {
		        		            	
//		        		            	
		        		            	userNameAndHisStats = new ArrayList();
		        		            	
		        		            	userNameAndHisStats.add(user_name);
		        		            	userNameAndHisStats.add( resultSet_getDifferenceOfEachEmployee.getDouble(1) );
		        		            	
		        		            	averageTimeTakenPerEngineer.add( userNameAndHisStats );
		        		            	
		        		            }else {
		        		            	System.out.println("unable to add the avg value of mysql query into array list ");
		        		            }
		        		        } catch (SQLException e) {
		        					e.printStackTrace();
		        				}
		        		    } catch (SQLException e1) {
		        				e1.printStackTrace();
		        			}	        		
		        	} // END : while
		        	
		        }
		    }

		return averageTimeTakenPerEngineer;
	}
	
	public Double getStatsOfPriority(Connection con, Integer priority /* 1 | 5 | 10 */) {
		
		String priorityStats_query = ""
				+ "select avg(DATEDIFF(closed_date, start_date)) " + 
				"from ticket " + 
				"where priority = ? ; ";

		try (
		        PreparedStatement preparedStatement_priorityStats = con.prepareStatement(priorityStats_query);
		    ) {
				
				preparedStatement_priorityStats.setInt(1, priority);
			
		        try (ResultSet resultSet_priorityStats = preparedStatement_priorityStats.executeQuery()) {
		        	
		            if (resultSet_priorityStats.next()) {
		            	
		            	return resultSet_priorityStats.getDouble(1);

		            }else {
		            	System.out.println("unable to add the avg value of mysql query into array list ");
		            }
		        } catch (SQLException e) {
					e.printStackTrace();
				}
		    } catch (SQLException e1) {
				e1.printStackTrace();
			}
		
		return null;
	}
	

	public ArrayList<ArrayList> getAgingOfOpenTicket(Connection con) throws SQLException{
		
		System.out.println("Inside getAgingOfOpenTicket(Connection con)");
		
		String query = ""
				+ "SELECT "
				+ "		ticket_id, "
				+ "		issue_category, "
				+ "		priority, "
				+ "		start_date, "
				+ "		TIMESTAMPDIFF(DAY, start_date , CURDATE() ) ,"	
				+ "		status, "
				+ "		service_engineer_id, "
				+ "		message "
				+ "	FROM ticket "
				+ "WHERE status = 'pending' or status = 'on_going'  ; ";
		
		ArrayList<ArrayList> agingOfOpenTickets = new ArrayList<ArrayList>();
		
		ArrayList eachOpenTicket = null;
		
		LocalDate start_date = null;
		Integer ticket_id = null;
		String issue_category = null;
		Integer priority = null;
		
		Integer AGE = null;
		
		String status = null;
		String message = null;
		Integer service_engineer_id = null;
		
		
		try (
				PreparedStatement preparedStatement_getAgingOfOpenTicket = con.prepareStatement(query);
		    ) {

		        try (ResultSet resultSet_getAgingOfOpenTicket = preparedStatement_getAgingOfOpenTicket.executeQuery()) {
		            if (resultSet_getAgingOfOpenTicket.next()) {
		            		

		            	start_date = resultSet_getAgingOfOpenTicket.getDate("start_date").toLocalDate();

		        		ticket_id = resultSet_getAgingOfOpenTicket.getInt("ticket_id");
		        		issue_category = resultSet_getAgingOfOpenTicket.getString("issue_category");
		        		priority = resultSet_getAgingOfOpenTicket.getInt("priority");
		        		
		        		AGE = resultSet_getAgingOfOpenTicket.getInt(5);
		        		
		        		status = resultSet_getAgingOfOpenTicket.getString("status");
		        		message = resultSet_getAgingOfOpenTicket.getString("message");
		        		service_engineer_id = resultSet_getAgingOfOpenTicket.getInt("service_engineer_id");

		            	eachOpenTicket = new ArrayList();
		            	
		            	
		            	eachOpenTicket.add(ticket_id);
		            	eachOpenTicket.add(issue_category);
		            	
		            	eachOpenTicket.add(priority);
		            	
		            	eachOpenTicket.add(start_date);
		            	
		            	eachOpenTicket.add(AGE);
		            	eachOpenTicket.add(status);
		            	eachOpenTicket.add(service_engineer_id);
		            	
		            	
		            	eachOpenTicket.add(message);
		            	System.out.println("adding message" + message);


		            	
		            	
		            	System.out.println("AGE = " + AGE);
		            	
		            	
		            	
		            	
		            	
		            	
		            	agingOfOpenTickets.add( eachOpenTicket );
		            }
		        }
		    }
		
		return agingOfOpenTickets;
	}
}

