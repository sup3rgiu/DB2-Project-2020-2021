package it.polimi.db2.gma.filters;

import it.polimi.db2.gma.entities.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet Filter implementation class LoginChecker
 */
public class BlockedChecker implements Filter {

	public BlockedChecker() {
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.print("User Blocked checker filter executing ...\n");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();

		User user = (User) session.getAttribute("user");
		if (user.isBlocked()) {
			String homepath = req.getServletContext().getContextPath() + "/Home";
			res.sendRedirect(homepath);
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	public void destroy() {
	}

}
