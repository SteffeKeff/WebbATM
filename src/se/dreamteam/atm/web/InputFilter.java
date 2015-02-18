package se.dreamteam.atm.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public final class InputFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		final String action = request.getParameter("action");
		final HashSet<String> validActions = new HashSet<>();
		
		validActions.add("addbank");
		validActions.add("addcustomer");
		validActions.add("login");
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		
		for(Entry<String, String[]> e: parameterMap.entrySet()){
			if(e.getKey().trim().isEmpty() || e.getValue()[0].trim().isEmpty()){
				httpResponse.sendError(400, "invalid parameters");
				return;
			}
		}
		
		if(validActions.contains(action)){
			chain.doFilter(request, response);
			return;
		}
		httpResponse.sendError(400, "invalid action");
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
