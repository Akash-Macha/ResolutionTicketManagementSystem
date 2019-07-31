package com.serviceTicketResolutionSystem.JavaBean;

import java.time.LocalDate;

public class Ticket implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6970754521618842407L;
	
	private Integer ticket_id;
	private String issue_category;
	private String message;
	private Integer end_user_id;
	private Integer priority; // 1 5 10
	private LocalDate start_date;
	private LocalDate requested_end_date;
	private String status;
	private Integer service_engineer_id;
	
	private String raised_by_user_name;
	


	// default constructor
	public Ticket() {
		
	}
	
	// getter and setters
	public Integer getTicket_id() {
		return ticket_id;
	}
	public void setTicket_id(Integer ticket_id) {
		this.ticket_id = ticket_id;
	}
	public String getIssue_category() {
		return issue_category;
	}
	public void setIssue_category(String issue_category) {
		this.issue_category = issue_category;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getEnd_user_id() {
		return end_user_id;
	}
	public void setEnd_user_id(Integer end_user_id) {
		this.end_user_id = end_user_id;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public LocalDate getStart_date() {
		return start_date;
	}
	public void setStart_date(LocalDate start_date) {
		this.start_date = start_date;
	}
	public LocalDate getRequested_end_date() {
		return requested_end_date;
	}
	public void setRequested_end_date(LocalDate requested_end_date) {
		this.requested_end_date = requested_end_date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getService_engineer_id() {
		return service_engineer_id;
	}
	public void setService_engineer_id(Integer service_engineer_id) {
		this.service_engineer_id = service_engineer_id;
	}
	public String getRaised_by_user_name() {
		return raised_by_user_name;
	}

	public void setRaised_by_user_name(String raised_by_user_name) {
		this.raised_by_user_name = raised_by_user_name;
	}
	
}
