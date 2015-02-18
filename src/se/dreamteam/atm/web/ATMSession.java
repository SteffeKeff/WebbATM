package se.dreamteam.atm.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import se.dreamteam.atm.exception.ATMException;
import se.dreamteam.atm.model.ATMReceipt;
import se.dreamteam.atm.service.ATMSessionImpl;

public final class ATMSession extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		ATMSessionImpl atmSession = (ATMSessionImpl) session.getAttribute("session");
		if(!(atmSession == null)){
			response.setContentType("text/html");
			switch(request.getParameter("action")){
				case "checkbalance": checkBalance(request, response, atmSession); break;
				case "deposit": addAmount(request, response, atmSession); break;
				case "withdraw": withdrawAmount(request, response, atmSession); break;
				case "receipt": getReceipt(request, response, atmSession); break;
			}
			return;
		}
		response.sendError(401, "not authenticated");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		ATMSessionImpl atmSession = (ATMSessionImpl) session.getAttribute("session");
		if(atmSession != null){
			response.setContentType("text/html");
			if("login".equals(request.getParameter("action"))){
				login(request, response, session);
			}
		}
	}
	
	private void login(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException
	{
		if(session != null){
			response.getWriter().println("Welcome  " + session.getAttribute("name") + "!");
			response.getWriter().println("<form method=\"GET\" action=\"ATMSession\"><button type=\"submit\" name=\"action\" VALUE=\"checkbalance\" >check balance</button></form>");
			response.getWriter().println("<form method=\"GET\" action=\"ATMSession\"><input type=\"text\" name=\"amount\" placeholder=\"amount\"/><button type=\"submit\" name=\"action\" VALUE=\"deposit\" >deposit</button></form>");
			response.getWriter().println("<form method=\"GET\" action=\"ATMSession\"><input type=\"text\" name=\"amount\" placeholder=\"amount\"/><button type=\"submit\" name=\"action\" VALUE=\"withdraw\" >withdraw</button></form>");
			response.getWriter().println("<form method=\"GET\" action=\"ATMSession\"><button type=\"submit\" name=\"action\" VALUE=\"receipt\" >get receipt</button></form>");
			response.getWriter().println("<form action=\"/Webb-ATM\"><button type=\"submit\">Log out</button></form>");
			return;
		}
		response.sendError(401, "Your not allowed to access this content");
	}
	
	private void checkBalance(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		try{
			response.getWriter().println("Current Balance: " + atmSession.checkBalance());
			response.getWriter().println("<form method=\"POST\" action=\"ATMSession\"><button type=\"submit\" name=\"action\" VALUE=\"login\">back</button></form>");
		}catch(ATMException e){
			response.sendError(400, e.getMessage());
		}
	}

	private void addAmount(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		atmSession.deposit(Integer.parseInt(request.getParameter("amount")));
		response.getWriter().println(request.getParameter("amount") + " is deposited");
		response.getWriter().println("<form method=\"POST\" action=\"ATMSession\"><button type=\"submit\" name=\"action\" VALUE=\"login\">back</button></form>");
	}

	private void withdrawAmount(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		try{
			atmSession.withdrawAmount(Integer.parseInt(request.getParameter("amount")));
			response.getWriter().println("You have withdrawn: " +request.getParameter("amount"));
			response.getWriter().println("<form method=\"POST\" action=\"ATMSession\"><button type=\"submit\" name=\"action\" VALUE=\"login\">back</button></form>");;
		}catch(ATMException e){
			response.sendError(400, e.getMessage());
		}
		
	}
	
	private void getReceipt(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		try{
			ATMReceipt receipt = atmSession.requestReceipt(atmSession.getTransactionId());
			response.getWriter().println("Receipt" );
			response.getWriter().println("<br>Date: " + receipt.getDate());
			response.getWriter().println("<br>Amount: " + receipt.getAmount() );
			response.getWriter().println("<br>Id: " + receipt.getTransactionId());
			
			response.getWriter().println("<form method=\"POST\" action=\"ATMSession\"><button type=\"submit\" name=\"action\" VALUE=\"login\">back</button></form>");
		}catch(ATMException e){
			response.sendError(400, e.getMessage());
		}
	}

}
