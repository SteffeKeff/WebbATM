package se.dreamteam.atm.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import se.dreamteam.atm.exception.ATMException;
import se.dreamteam.atm.model.ATMReceipt;
import se.dreamteam.atm.service.ATMSessionImpl;

@WebServlet("/session/*")
public final class ATMSession extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static JSONObject jsonObject = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		final HttpSession session = request.getSession();
		final String id = (String) session.getAttribute("id");
		final ATMSessionImpl atmSession = (ATMSessionImpl) session.getAttribute(id);

		final String pathInfo = request.getPathInfo();
		final String[] pathSegements = pathInfo.split("/");

		if (pathSegements.length == 2)
		{
			final String path = pathSegements[1];

			if (atmSession != null)
			{
				response.setContentType("application/json");
				switch (path)
				{
				case "balance":
					checkBalance(request, response, atmSession);
					return;
				case "deposit":
					deposit(request, response, atmSession);
					return;
				case "withdraw":
					withdraw(request, response, atmSession);
					return;
				case "receipt":
					getReceipt(request, response, atmSession);
					return;
				}
			}
			else
			{
				response.sendError(401, "not authenticated");
			}
		}
		response.sendError(400, "bad request");
	}

	@SuppressWarnings("unchecked")
	private void checkBalance(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put("balance", atmSession.checkBalance());
			response.getWriter().print(json);
		}
		catch (ATMException e)
		{
			response.sendError(400, e.getMessage());
		}
	}

	private void deposit(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		setJsonObject(request, response);
		atmSession.deposit(Integer.parseInt(getString("amount", response)));
		response.getWriter().println(jsonObject);
	}

	private void withdraw(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		setJsonObject(request, response);
		try
		{
			atmSession.withdrawAmount(Integer.parseInt(getString("amount", response)));
			response.getWriter().println(jsonObject);
		}
		catch (ATMException e)
		{
			response.sendError(400, e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	private void getReceipt(HttpServletRequest request, HttpServletResponse response, ATMSessionImpl atmSession) throws IOException
	{
		try
		{
			ATMReceipt receipt = atmSession.requestReceipt(atmSession.getTransactionId());

			JSONObject json = new JSONObject();
			json.put("transactionId", receipt.getTransactionId());
			json.put("date", receipt.getDate());
			json.put("amount", receipt.getAmount());
			response.getWriter().print(json);

		}
		catch (ATMException e)
		{
			response.sendError(400, e.getMessage());
		}
	}

	private void setJsonObject(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		JSONParser parser = new JSONParser();
		try
		{
			jsonObject = (JSONObject) parser.parse(bufferedReader);
		}
		catch (ParseException e)
		{
			response.sendError(400, e.getMessage());
		}
	}

	private String getString(String value, HttpServletResponse response) throws IOException
	{
		try
		{
			return (String) jsonObject.get(value);
		}
		catch (NullPointerException e)
		{
			response.sendError(400, "no amount...");
		}
		return null;
	}
}
