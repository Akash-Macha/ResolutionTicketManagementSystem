package com.serviceTicketResolutionSystem.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.ArrayList;

import com.serviceTicketResolutionSystem.JavaBean.Ticket;

public class EndUserDAO implements IEndUser{
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
	
	@Override
	public boolean generateTicket(Connection con, Ticket ticket) throws SQLException {

		
		/* set the "end_user_id" in ticket table from   login table  using   user_name */
		String getEndUserId_query = ""
				+ "SELECT user_id "
				+ "FROM login "
				+ "WHERE user_name = ? ; ";
		PreparedStatement ps = con.prepareStatement(getEndUserId_query);
	    ps.setString(1, ticket.getRaised_by_user_name());

	    // process the results
	    ResultSet rs = ps.executeQuery();
	    
	    int end_user_id = -1;
	    if ( rs.next() )
	    	end_user_id = rs.getInt(1);
	    else
	    	System.out.println(" getEndUserId_query Failed !");
	    
	    System.out.println("end_user_id = " + end_user_id);
	    ticket.setEnd_user_id(end_user_id);
	    	
		/* INSERT INTO TICKET TABLE
		 * 
		 * 	create table ticket(
			ticket_id int(50) unique not null,
		    issue_category varchar(100), -- software_installation | cleaning
		    end_user_id int(50),
		    foreign key (end_user_id) REFERENCES  login(user_id),
		    priority  int (10), -- low | max | med  ||  1 5 10
		    start_date date,
		    requested_end_date date,
		    status varchar(20), -- on_progress | pending | closed
		    service_engineer_id int(50),
		    foreign key (service_engineer_id) REFERENCES  login(user_id)
);
		 */
		String query = "INSERT INTO ticket "
				+ "(issue_category, "
				+ "end_user_id, "
				+ "priority, "
				+ "start_date, "
				+ "requested_end_date, "
				+ "status, "
				+ "service_engineer_id, "
				+ "message ) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?) ; ";
		
		PreparedStatement insert_into_ticket = con.prepareStatement(query);		

		insert_into_ticket.setString(1, ticket.getIssue_category());
		insert_into_ticket.setInt(2, ticket.getEnd_user_id());
		insert_into_ticket.setInt(3, ticket.getPriority());
		
		System.out.println("ticket.getStart_date() = " + ticket.getStart_date());
		
		insert_into_ticket.setDate(4, java.sql.Date.valueOf(ticket.getStart_date()));//ticket.getStart_date());
		insert_into_ticket.setDate(5, java.sql.Date.valueOf(ticket.getRequested_end_date()));
		
		// initial status of any ticket
		insert_into_ticket.setString(6, "Pending"); //ticket.getStatus()
		
		// initially not assigned to any serviceEngineer
		insert_into_ticket.setInt(7, -1); //ticket.getService_engineer_id()
		
		insert_into_ticket.setString(8, ticket.getMessage()); //ticket.getService_engineer_id()
		
		System.out.println("CHECK 120  = ticket.getMessage()" + ticket.getMessage());


		if(insert_into_ticket.executeUpdate() >= 1) {
			System.out.println("inserted insert_into_ticket.executeUpdate()");			

		}
		
		/* get and set last inserted ticket_id */
		String getLastInsertedId = ""
				+ "SELECT ticket_id "
				+ "FROM ticket "
				+ "ORDER BY ticket_id DESC ;";
		
		PreparedStatement getLastInsertedId_ps = con.prepareStatement(getLastInsertedId);
		
		ResultSet getLastInsertedId_rs = getLastInsertedId_ps.executeQuery();
		
		int lastInsertedTicketID = -1;
		if(getLastInsertedId_rs.next()) {
			lastInsertedTicketID = getLastInsertedId_rs.getInt(1);
			
			System.out.println("lastInsertedTicketID = " + lastInsertedTicketID);
			
			ticket.setTicket_id(lastInsertedTicketID);
			
			getLastInsertedId_rs.close();
			
			return true;
		}

		return false;
	}
	
	public boolean checkAndAssignTicketForUnAssignedServiceEmployee(Connection con, Ticket ticket) throws SQLException {
		
		/* get all the unAssigned service employees and Assign the ticket to the First Service Employee */
		/* we are mapping  Ticket: Issue_category   with serviceEmployee: area_of_expertise  */
		
		String getunAssignedServiceEmployeesQuery = ""
				+ "SELECT service_engineer_id "
				+ "FROM service_engineer "
				+ "WHERE area_of_expertise = ? "
				+ "and current_high_prority_ticket_id = -1 "
				+ "ORDER BY total_tickets_worked_on ; "; // *********  Ordering by total_tickets_worked_on: less tickets worked on will
														//				HIHG priority
		
		PreparedStatement getunAssignedServiceEmployees_ps = con.prepareStatement(getunAssignedServiceEmployeesQuery);
		getunAssignedServiceEmployees_ps.setString(1, ticket.getIssue_category());
		
	    ResultSet rs = getunAssignedServiceEmployees_ps.executeQuery();
	    if ( rs.next() ){
	    	/* HURRAY we got any unAssigned service Employee */ 
	    	
	    	/* get the ticket_id */
	    	/* you have assined ticket_id in ticket bean */
	    	
	    	
	    	/*	updating service_engineer table
	    	 * 
	    	 *  1. update  current_high_prority_ticket_id = ticket.getTicketId()
	    	 *  2. update 	current_ticket_start_date = today!
	    	 *  			priority = ticket.getPriority() 			// <----------------------  ADDED
	    	 *  
	    	 *  EXCLUDED [ 3. update   priority = ticket.getPriority() ] get the priority using INNER JOIN
	    	*/
	    	
	    	
	    	int service_engineer_id = rs.getInt(1);
	    	
	    	
	    	// update_current_high_prority_ticket_id_and_current_ticket_start_date_query
	    	// updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_query
	    	String updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_query = ""
	    			+ "UPDATE service_engineer "
	    			+ "SET current_ticket_start_date = ?, "
	    			+ "		current_high_prority_ticket_id = ?, "
	    			+ "		priority = ? " // <-------------------------------------- has been ADDED !
	    			+ "WHERE service_engineer_id = ? ; ";
	    	
			PreparedStatement updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_ps = con.prepareStatement(updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_query);
			
			System.out.println("-- CHECK- 1: ticket.getTicket_id() =  " + ticket.getTicket_id());
			
			updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_ps.setDate(1,  java.sql.Date.valueOf( LocalDate.now() )); // today
			updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_ps.setInt(2, ticket.getTicket_id() ); 

			updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_ps.setInt(3, ticket.getPriority()); // ADDED
			
			updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_ps.setInt(4, service_engineer_id);
			
			if(updateCurrentHighPriorityTicketIdAndCurrentTicketStartDateAndPriority_ps.executeUpdate() >= 1) {
				System.out.println("UPDATED the serviceEmployee table");
				
				/* UPDATED the serviceEmployee table */
				
				/* !! update the ticket table !! */
				/*
				 * 1. put status as on going
				 * 2. assign the service_engineer_id to the current ticket
				 */
				String updateTicketTable_query = ""
						+ "UPDATE ticket "
						+ "SET 	status = 'on_going', "
						+ "		service_engineer_id = ? "
						+ "WHERE ticket_id = ? ; ";
				
				PreparedStatement updateTicketTable_ps = con.prepareStatement(updateTicketTable_query);
				updateTicketTable_ps.setInt(1, service_engineer_id);
				updateTicketTable_ps.setInt(2, ticket.getTicket_id());
				
				if(updateTicketTable_ps.executeUpdate() >= 1) {
					System.out.println("Successfully upated the ticket table: status & service_engineer_id");
					
					return true;
				}
				
			}
	    }else {
	    	System.out.println("No employee is free !! ");
	    }
		
		return false;
	}
	
	public boolean checkForLowPriorityTicketServiceEmployee(Connection con, Ticket ticket) throws SQLException {
		/*
		 * THE APPROACH:
		 * 
		 * the new Ticket priority is High = 10
		 * 
		 * searching for an service_employee who is working with LOW priority ticket : ticket.priority = 1
		 * where ticket.issue_category = service_engineer.area_of_expertise
		 * 
		 * if found:
		 * 		1. update the ticket with Low Priority 
		 * 				status for the found Low priority ticket as 'pending'
		 * 
		 * 				SET priority = 'pending'
		 * 					service_engineer_id = -1 
		 * 				WHERE ticket_id = ? 			-- ticket.getTicket_id()
		 * 
		 * 		1.2
		 * 			update the new Ticket
		 * 				SET status = 'on_going'
		 * 					service_engineer_id = ? 	-- assign from the result set
		 * 
		 * 		2. update the service_engineer
		 * 				SET current_high_prority_ticket_id = now()
		 * 					current_high_prority_ticket_id = ?	-- assign from the result set
		 * 				WHERE service_engineer_id = ?			-- assign from the result set
		 *
		 */
		
		String OLD_getLowPriorityTicketServiceEmployee_query = ""
                + "SELECT " + 
                "  service_engineer.service_engineer_id, " + 
                "  ticket.ticket_id " + 
                "from " + 
                "  service_engineer, " + 
                "  ticket " + 
                "where " + 
                "  ticket.ticket_id = service_engineer.current_high_prority_ticket_id " + 
                "  and service_engineer.area_of_expertise = ticket.issue_category " + 
                "  and ticket.priority = 1 "
                + "and ticket.issue_category = ? " + 
                "ORDER by " + 
                "  ticket.ticket_id desc ; "; // <-** POINT 4 : assign to the one who is working on the most recently created low priority ticket
		/* the latest / lastly created ticket will be the MOST RECENTLY CREATED TICKET ! */
		
		// New updated query - WORKING !
		String getLowPriorityTicketServiceEmployee_query = ""
				+ "SELECT "
				+ "		service_engineer_id, "
				+ "		current_high_prority_ticket_id "
				+ "FROM "
				+ "		service_engineer "
				+ "WHERE "
				+ "		priority = 1 "
				+ "		and area_of_expertise = ? "  //  area_of_expertise  == ticket.issue_category
				+ "ORDER BY "
				+ "		current_high_prority_ticket_id DESC ; ";   //  == ticket.ticket_id desc
				
			    
	    try (
	    		PreparedStatement preparedStatement_getLowPriorityTicketServiceEmployee = con.prepareStatement(getLowPriorityTicketServiceEmployee_query);
	        ) {
	    	
	    		preparedStatement_getLowPriorityTicketServiceEmployee.setString(1, ticket.getIssue_category());

	            try (ResultSet resultSet_getLowPriorityTicketServiceEmployee = preparedStatement_getLowPriorityTicketServiceEmployee.executeQuery()) {
	            	/* we need only the 1st service_engineer, because we are sorting them in descending order */
	            	
	                if (resultSet_getLowPriorityTicketServiceEmployee.next()) {
	                	
	                	/* Yayy! You've found an service_engineer with LOW priority Ticket */
	                	/*
	                	 * You've | service_engineer_id | ticket_id |  in the resultSet
	                	 */
	                	
	                	
	                	Integer service_engineer_id = resultSet_getLowPriorityTicketServiceEmployee.getInt("service_engineer_id");
	                	Integer ticket_id = resultSet_getLowPriorityTicketServiceEmployee.getInt("current_high_prority_ticket_id");
	                	
	                	System.out.println("service_engineer_id -> " + service_engineer_id);
	                	System.out.println("ticket_id -> " + ticket_id); // 
	                	
	                	/*  
						 * 		1. update the low priority ticket 
						 * 				status for the found Low priority ticket as 'pending'
						 * 
						 * 				SET priority = 'pending',
						 * 					service_engineer_id = -1
						 * 				WHERE ticket_id = ? 			-- assign from the result set
	                	 *  */
	                	String updateTicketTable_query = "" + 
	                			"UPDATE ticket " + 
	                			"SET status = 'pending', " +
	                			"	 service_engineer_id = -1 " + 
	                			"WHERE ticket_id = ? ; ";
	                	
	                	PreparedStatement preparedStatement_updateTicketTable = con.prepareStatement(updateTicketTable_query);
	                	preparedStatement_updateTicketTable.setInt(1, resultSet_getLowPriorityTicketServiceEmployee.getInt("current_high_prority_ticket_id"));
	                	
	                	if(preparedStatement_updateTicketTable.executeUpdate() >= 1) {
	                		System.out.println("updated Low priority ticket in ticket table!");
	                		
	                		
	                		/* START: updating the new Ticket */
	                		System.out.println("\nNew Ticket Id = " + ticket.getTicket_id() + "\n");
	                		
	                		String updateNewTicket_query = ""
	                				+ "Update ticket "
	                				+ "SET 	status = 'on_going', "
	                				+ "		service_engineer_id = ? "
	                				+ "WHERE ticket_id = ? ; ";
	                		
	                		
		                	PreparedStatement preparedStatement_updateNewTicket_query = con.prepareStatement(updateNewTicket_query);
		                	
		                	preparedStatement_updateNewTicket_query.setInt( 1, resultSet_getLowPriorityTicketServiceEmployee.getInt("service_engineer_id") );
		                	preparedStatement_updateNewTicket_query.setInt( 2, ticket.getTicket_id() );
		                	
		                	if( preparedStatement_updateNewTicket_query.executeUpdate() >= 1 )
		                		System.out.println("\nUpdated the new ticket record\n");
	                		
		                	/* END: updating the new Ticket */
	                		
	                		
	                		
		                	/* After updating '''ticket table'''
		                	 * Updating '''service_engineer table''' 
		                	 * 
		                	 * */
		                	String updateServiceEngineerTable_query = "" +
		                			"UPDATE service_engineer " + 
		                			"SET current_ticket_start_date = ?, " + 
		                			"    current_high_prority_ticket_id = ?, " +
		                			"    priority = ? " + 			// 		<-------------  ADDED
		                			"WHERE service_engineer_id = ? ; ";
	                		
		                	PreparedStatement preparedStatement_updateServiceEngineerTable = con.prepareStatement(updateServiceEngineerTable_query);
		                	
		                	preparedStatement_updateServiceEngineerTable.setDate(1, java.sql.Date.valueOf( LocalDate.now() ));
		                	preparedStatement_updateServiceEngineerTable.setInt(2, ticket.getTicket_id() );
		                	
		                	preparedStatement_updateServiceEngineerTable.setInt(3, ticket.getPriority() ); // <------ ADDED !
		                	
		                	preparedStatement_updateServiceEngineerTable.setInt(4, resultSet_getLowPriorityTicketServiceEmployee.getInt("service_engineer_id"));
		                	
		                	if(preparedStatement_updateServiceEngineerTable.executeUpdate() >= 1) {
		                		
		                		
		                		/* successfully assigned new ticket (priority: high) with low priority ticket */
		                		System.out.println("successfully assigned new ticket (priority: high) with low priority ticket");
		                		
		                		return true;
		                	}
		                	
	                	}else {
	                		System.out.println("FAILED : unable to update ticket table!");
	                	}
	                	
	                }
	            }
	        }
	    
		return false;
	}
	
	public boolean checkForMediumPriorityTicketServiceEmployee(Connection con, Ticket ticket) throws SQLException {
		/*
		 * THE APPROACH:
		 * 
		 * the new Ticket priority is HIGH = 10
		 * 
		 * 
		 * searching for an service_employee who is working with MEDIUM priority ticket : ticket.priority = 5
		 * where ticket.issue_category = service_engineer.area_of_expertise
		 * 
		 * if found:
		*      1. update the ticket with MEDIUM Priority 
		*              status for the found MEDIUM priority ticket as 'pending'
		* 
		*              SET priority = 'pending'
		*                  service_engineer_id = -1 
		*              WHERE ticket_id = ?             -- ticket.getTicket_id()
		 * 
		 * 
		 * 
		 *  
		 */
		
		String OLD_getMediumPriorityTicketServiceEmployee_query = ""
                + "SELECT " + 
                "  service_engineer.service_engineer_id, " + 
                "  ticket.ticket_id " + 
                "from " + 
                "  service_engineer, " + 
                "  ticket " + 
                "where " + 
                "  ticket.ticket_id = service_engineer.current_high_prority_ticket_id " + 
                "  and service_engineer.area_of_expertise = ticket.issue_category " + 
                "  and ticket.priority = 5 " + 
                "ORDER by " + 
                "  ticket.ticket_id desc ; "; // <-** POINT 4 : assign to the one who is working on the most recently created low priority ticket
		/* the latest / lastly created ticket will be the MOST RECENTLY CREATED TICKET ! */
		
		String getMediumPriorityTicketServiceEmployee_query = ""
				+ "SELECT "
				+ "		service_engineer_id, "
				+ "		current_high_prority_ticket_id "
				+ "FROM "
				+ "		service_engineer "
				+ "WHERE "
				+ "		priority = 5 "
				+ "		and area_of_expertise = ? "  // <------  area_of_expertise  == ticket.issue_category
				+ "ORDER BY "
				+ "		current_high_prority_ticket_id DESC ; ";   //  == ticket.ticket_id desc
		
	    try (
	    		PreparedStatement preparedStatement_getMediumPriorityTicketServiceEmployee = con.prepareStatement(getMediumPriorityTicketServiceEmployee_query);
	        ) {

	    	
	    		// setting additional parameter,
	    		preparedStatement_getMediumPriorityTicketServiceEmployee.setString(1, ticket.getIssue_category() );
	    		
	            try (ResultSet resultSet_getMediumPriorityTicketServiceEmployee = preparedStatement_getMediumPriorityTicketServiceEmployee.executeQuery()) {
	            	/* we need only the 1st service_engineer, because we are sorting them in descending order */
	            	
	                if (resultSet_getMediumPriorityTicketServiceEmployee.next()) {
	                	
	                	/* Yayy! You've found an service_engineer with ""MEDIUM"" priority Ticket */
	                	/*
	                	 * You've | service_engineer_id | ticket_id |  in the resultSet
	                	 */
	                	
	                	
	                	Integer service_engineer_id = resultSet_getMediumPriorityTicketServiceEmployee.getInt("service_engineer_id");
	                	//Integer ticket_id = resultSet_getMediumPriorityTicketServiceEmployee.getInt("ticket_id");
	                	Integer ticket_id = resultSet_getMediumPriorityTicketServiceEmployee.getInt("current_high_prority_ticket_id");
	                	
	                	System.out.println("service_engineer_id -> " + service_engineer_id);
	                	System.out.println("ticket_id / current_high_prority_ticket_id -> " + ticket_id); // 39
	                	
	                	/*  
						 * 		1. update the MEDIUM priority ticket 
						 * 				status for the found MEDIUM priority ticket as 'pending'
						 * 
						 * 				SET priority = 'pending',
						 * 					service_engineer_id = -1
						 * 					priority = ? 				<-----------  ADDED
						 * 				WHERE ticket_id = ? 			-- assign from the result set
	                	 *  */
	                	String updateTicketTable_query = "" + 
	                			"UPDATE ticket " + 
	                			"SET status = 'pending', " + 
	                			"	 service_engineer_id = -1 " + 
	                			"WHERE ticket_id = ? ; ";
	                	
	                	
	                	PreparedStatement preparedStatement_updateTicketTable = con.prepareStatement(updateTicketTable_query);
	                	//preparedStatement_updateTicketTable.setInt(1, resultSet_getMediumPriorityTicketServiceEmployee.getInt("ticket_id"));
	                	preparedStatement_updateTicketTable.setInt(1, resultSet_getMediumPriorityTicketServiceEmployee.getInt("current_high_prority_ticket_id"));
	                	
	                	if(preparedStatement_updateTicketTable.executeUpdate() >= 1) {
	                		System.out.println("updated MEDIUM priority ticket [ made it 'pending' ] in ticket table!");
	                		
	                		
	                		/* START: updating the new Ticket */
	                		System.out.println("\nNew Ticket Id = " + ticket.getTicket_id() + "\n"); // 40 +
	                		
	                		String updateNewTicket_query = ""
	                				+ "UPDATE ticket "
	                				+ "SET 	status = 'on_going', "
	                				+ "		service_engineer_id = ? "
	                				+ "WHERE ticket_id = ? ; ";
	                		
		                	PreparedStatement preparedStatement_updateNewTicket_query = con.prepareStatement(updateNewTicket_query);
		                	
		                	preparedStatement_updateNewTicket_query.setInt( 1, resultSet_getMediumPriorityTicketServiceEmployee.getInt("service_engineer_id") );
		                	preparedStatement_updateNewTicket_query.setInt( 2, ticket.getTicket_id() );
		                	
		                	if( preparedStatement_updateNewTicket_query.executeUpdate() >= 1 )
		                		System.out.println("\nUpdated the new ticket record\n");
	                		
		                	/* END: updating the new Ticket */
	                	
	                	
		                	/* After updating '''ticket table'''
		                	 * Updating '''service_engineer table''' 
		                	 * 
		                	 * */
		                	String OLD_updateServiceEngineerTable_query = "" +
		                			"UPDATE service_engineer " + 
		                			"SET current_ticket_start_date = ?, " + 
		                			"    current_high_prority_ticket_id = ? " + 
		                			"WHERE service_engineer_id = ? ; ";
		                	
		                	String updateServiceEngineerTable_query = "" +
		                			"UPDATE service_engineer " + 
		                			"SET current_ticket_start_date = ?, " + 
		                			"    current_high_prority_ticket_id = ?, " +
		                			"    priority = ? " + // <--------------------------  ADDED 
		                			"WHERE service_engineer_id = ? ; ";
	                		
		                	PreparedStatement preparedStatement_updateServiceEngineerTable = con.prepareStatement(updateServiceEngineerTable_query);
		                	
		                	preparedStatement_updateServiceEngineerTable.setDate(1, java.sql.Date.valueOf( LocalDate.now() ));
		                	preparedStatement_updateServiceEngineerTable.setInt(2, ticket.getTicket_id() );
		                	
		                	preparedStatement_updateServiceEngineerTable.setInt(3, ticket.getPriority() ); // <----------------------- ADDED
		                	
		                	preparedStatement_updateServiceEngineerTable.setInt(4, resultSet_getMediumPriorityTicketServiceEmployee.getInt("service_engineer_id"));
		                	
		                	if(preparedStatement_updateServiceEngineerTable.executeUpdate() >= 1) {
		                		
		                		
		                		/* successfully assigned new ticket (priority: high) with low priority ticket */
		                		System.out.println("successfully assigned new ticket (priority: high) with MEDIUM priority ticket");
		                		
		                		return true;
		                	}
		                	
	                	}else {
	                		System.out.println("FAILED : unable to update ticket table!");
	                	}
	                	
	                }
	            }
	    }
		return false;
	}
	

	
	public ArrayList<Ticket> getAllTicketsInAnArrayList(Connection con, String raised_by_user_name) throws SQLException {
		
			System.out.println("EndUserDAO - getAllTicketsInAnArrayList  called");
			
			/*
			 *   The Approach:
			 *   	
			 *   											
			 */
			
			String getAllRecordsOfThisEndUser_query = ""
					+ "SELECT "
					+ "		ticket.ticket_id, "
					+ "		ticket.issue_category, "
					+ "		ticket.message, "
					+ "		ticket.start_date, "
					+ "		ticket.requested_end_date, "
					+ "		ticket.service_engineer_id, "
					+ "		ticket.priority, "
					+ "		ticket.status " + 
					"FROM "
					+ "		login, "
					+ " 	ticket " + 
					"WHERE "
					+ "		login.user_id = ticket.end_user_id "
					+ "		and login.user_name = ? ; " ;

			PreparedStatement preparedStatement_getAllRecordsOfThisEndUser = con.prepareStatement(getAllRecordsOfThisEndUser_query);
			System.out.println("End_user_id used to retrieve data from ticket table = " + raised_by_user_name);
			preparedStatement_getAllRecordsOfThisEndUser.setString(1, raised_by_user_name); // raised_by_user_name == end_user_id

		    
		    ArrayList<Ticket> listOfTickets = new ArrayList<Ticket>();   /*  https://stackoverflow.com/a/10417020 */
		    
		    ResultSet listOfTickets_rs = preparedStatement_getAllRecordsOfThisEndUser.executeQuery();
		    
		    System.out.println("Query has been executed");
		    
		    Ticket ticket = null;
		    
		    while ( listOfTickets_rs.next() ) {
		    	System.out.println("Line: 250 - Entered into while loop");
		    	
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
}
