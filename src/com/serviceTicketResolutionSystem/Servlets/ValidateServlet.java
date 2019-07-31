package com.serviceTicketResolutionSystem.Servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.serviceTicketResolutionSystem.DAO.ValidateDAO;

/**
 * Servlet implementation class ValidateServlet
 */
@WebServlet("/ValidateServlet")
public class ValidateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ValidateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/**/
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setHeader("Expires", "0"); // Proxies.		
		/**/
		
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String user_name = request.getParameter("user_name");
		String password = request.getParameter("password");
		
		ValidateDAO validateDAO = new ValidateDAO();
		
		Connection con = null;
		String role = "Invalid_User";
		try {
			con = validateDAO.getConnection();
			
			role = validateDAO.validateUser(con, user_name, password);
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("role = " + role);
		
		/* creating session */
		HttpSession session = request.getSession();
		
		/* setting user_name into session  */
		session.setAttribute("user_name", user_name);
		
		if(role.equals("end_user")) {
			
			System.out.println("Redirecting to End User.jsp file");
						
			/* redirecting to EndUser.jsp */
			response.sendRedirect("EndUser.jsp");
			
		}else if(role.equals("service_engineer")) {
			
			System.out.println("Service_Engineer");
			
			response.sendRedirect("ServiceEngineer.jsp");

		}else {
			/* invalid user */
			response.sendRedirect("index.jsp?isvalid=false");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setHeader("Expires", "0"); // Proxies.
		
		
		doGet(request, response);
	}

}
 