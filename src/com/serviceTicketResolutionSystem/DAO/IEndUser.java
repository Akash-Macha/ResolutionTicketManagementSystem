package com.serviceTicketResolutionSystem.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import com.serviceTicketResolutionSystem.JavaBean.Ticket;

public interface IEndUser {
	
	public boolean generateTicket(Connection con, Ticket ticket) throws SQLException ;
}
