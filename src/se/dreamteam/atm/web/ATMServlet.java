package se.dreamteam.atm.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
import se.dreamteam.atm.exception.ATMSecurityException;
import se.dreamteam.atm.model.ATMCard;
import se.dreamteam.atm.service.ATM;
import se.dreamteam.atm.service.BankImpl;

@WebServlet("/*")
public final class ATMServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private ATM atm = new ATM();
	private static JSONObject jsonObject = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("application/json");
		@SuppressWarnings("rawtypes") ArrayList<HashMap> links = new ArrayList<>();
		HashMap<String, String> link1 = new HashMap<>();
		HashMap<String, String> link2 = new HashMap<>();
		HashMap<String, String> link3 = new HashMap<>();
		link1.put("href", "/banks");
		link1.put("rel", "self");
		link2.put("href", "/customers");
		link2.put("rel", "self");
		link3.put("href", "/login");
		link3.put("rel", "self");
		links.add(link1);
		links.add(link2);
		links.add(link3);

		JSONObject json = new JSONObject();
		json.put("links", links);

		response.getWriter().print(json);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// response.setContentType("application/json");
		final String pathInfo = request.getPathInfo();
		final String[] pathSegements = pathInfo.split("/");

		if (pathSegements.length == 2)
		{
			final String path = pathSegements[1];
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

			switch (path)
			{
			case "banks":
				addBank(getString("bankId"), response);
				return;
			case "customers":
				addCustomer(getString("card"), getString("bankId"), Integer.parseInt(getString("pin")), response);
				return;
			case "login":
				login(getString("card"), getString("bankId"), Integer.parseInt(getString("pin")), response, request);
				return;
			}
		}
		response.sendError(400, "bad request");
	}

	private void addBank(String bankId, HttpServletResponse response) throws IOException
	{
		if (!atm.hasBank(bankId))
		{
			atm.addBank(new BankImpl(bankId));
			response.setStatus(201);
		}
		else
		{
			response.sendError(400, "bank already exist");
		}
	}

	private void addCustomer(String accountHolderId, String bankId, int pin, HttpServletResponse response) throws IOException
	{
		if (atm.hasBank(bankId))
		{
			try
			{
				atm.addCard(new ATMCard(accountHolderId, bankId, pin));
				response.setStatus(201);
			}
			catch (ATMException e)
			{
				response.sendError(400, e.getMessage());
			}

		}
		else
		{
			response.sendError(400, "bank doesnt exist");
		}
	}

	@SuppressWarnings("unchecked")
	private void login(String cardHolderId, String bank, int pin, HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException
	{
		HttpSession session = request.getSession();
		try
		{

			String id = UUID.randomUUID().toString();
			session.setAttribute(id, atm.verifyPin(pin, new ATMCard(cardHolderId, bank, pin)));
			session.setAttribute("id", id);

			response.setContentType("application/json");
			JSONObject json = new JSONObject();

			@SuppressWarnings("rawtypes") ArrayList<HashMap> links = new ArrayList<>();
			HashMap<String, String> link1 = new HashMap<>();
			HashMap<String, String> link2 = new HashMap<>();
			HashMap<String, String> link3 = new HashMap<>();
			HashMap<String, String> link4 = new HashMap<>();
			link1.put("href", "/balance");
			link1.put("rel", "session");
			link2.put("href", "/deposit");
			link2.put("rel", "session");
			link3.put("href", "/withdraw");
			link3.put("rel", "session");
			link4.put("href", "/receipt");
			link4.put("rel", "session");
			links.add(link1);
			links.add(link2);
			links.add(link3);
			links.add(link4);

			json.put("links", links);
			response.getWriter().print(json);

		}
		catch (ATMSecurityException e)
		{
			response.sendError(401, e.getMessage());
		}
		catch (ATMException e)
		{
			response.sendError(400, e.getMessage());
		}
	}

	private String getString(String value)
	{
		return (String) jsonObject.get(value);
	}

}