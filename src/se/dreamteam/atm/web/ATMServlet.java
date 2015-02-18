package se.dreamteam.atm.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import se.dreamteam.atm.exception.ATMException;
import se.dreamteam.atm.exception.ATMSecurityException;
import se.dreamteam.atm.model.ATMCard;
import se.dreamteam.atm.service.ATM;
import se.dreamteam.atm.service.BankImpl;

public final class ATMServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	ATM atm = new ATM();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		switch(request.getParameter("action")){
			case "addbank": addBank(request.getParameter("bankid"), response); break;
			case "addcustomer": addCustomer(request.getParameter("card"), request.getParameter("bank"), Integer.parseInt(request.getParameter("pin")), response); break;
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if("login".equals(request.getParameter("action"))){
			response.setContentType("text/html");
			login(request.getParameter("card"), request.getParameter("bank"), Integer.parseInt(request.getParameter("pin")), response, request);
		}
	}
	
	private void addBank(String bankId, HttpServletResponse response) throws IOException
	{
		if(!atm.hasBank(bankId))
		{
			atm.addBank(new BankImpl(bankId)); 
			response.getWriter().println("Bank added");
			response.getWriter().println("<form action=\"/Webb-ATM\"><button type=\"submit\">back</button></form>");
		}else{
			response.sendError(400,"bank already exist");
		}
	}
	
	private void addCustomer(String accountHolderId, String bankId, int pin , HttpServletResponse response) throws IOException
	{
		if(atm.hasBank(bankId))
		{	
			try{
				atm.addCard(new ATMCard(accountHolderId, bankId, pin));
				response.getWriter().println("Card added");
				response.getWriter().println("<form action=\"/Webb-ATM\"><button type=\"submit\">back</button></form>");
			}catch(ATMException e){
				response.sendError(400, e.getMessage());
			}
			
		}else{
			response.sendError(400, "bank doesnt exist");
		}
	}
	
	private void login(String card, String bank, int pin, HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException
	{
		HttpSession session = request.getSession();
		try{
			session.setAttribute("session", atm.verifyPin(pin, new ATMCard(card, bank, pin)));
			session.setAttribute("name", card);
			
			RequestDispatcher rd = request.getRequestDispatcher("/ATMSession");
			rd.forward(request, response);
			
		}catch(ATMSecurityException e)
		{
			response.sendError(401, e.getMessage());
		}catch(ATMException e)
		{
			response.sendError(400, e.getMessage());
		}
		
	}

}