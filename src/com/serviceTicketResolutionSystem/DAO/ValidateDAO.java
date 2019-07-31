package com.serviceTicketResolutionSystem.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidateDAO {
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
	
	public String validateUser(Connection con, String user_name, String password) throws SQLException {

		System.out.println("validateUser called");

		String query = ""
				+ "select user_name, password, role, user_id "
				+ "FROM login "
				+ "WHERE user_name = ?;";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, user_name);

		// process the results
		ResultSet rs = ps.executeQuery();

		//	    rs.next();
		System.out.println("Before if block");
		if(rs.next()) {
			String DB_user_name = rs.getString(1);
			String DB_password = rs.getString(2);

			System.out.println("Usename  = " + DB_user_name);
			System.out.println("Password  = " + DB_password);

			System.out.println("passed user: " + user_name);
			System.out.println("passed password: " + password);

			if (rs.getString(1).equals(user_name) && rs.getString(2).equals(password)) {
				// Valid user;
				System.out.println("valid user");
				return rs.getString(3);
			}
		}

		System.out.println("After if block\nNOT a valid user");
		return "InvalidUser";
	}

}
