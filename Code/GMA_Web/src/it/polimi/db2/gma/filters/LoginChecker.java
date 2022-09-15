package it.polimi.db2.gma.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet Filter implementation class LoginChecker
 */
public class LoginChecker implements Filter {

	public LoginChecker() {
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
		System.out.print("Login checker filter executing ...\n");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		String basePath = req.getServletContext().getContextPath() + "/"; // which is also loginpath if user is not already logged

		String URI = req.getRequestURI();

		HttpSession session = req.getSession();
		if (URI.matches(".*(css|jpg|png|gif|js)")) {
		} // bypass the filter for this file extensions
		else if (session.isNew() || session.getAttribute("user") == null) {
			if (!(URI.equals(loginpath) || URI.equals(basePath + "CheckLogin") || URI.equals(basePath + "Signup"))) { // let continue the filter chain if the URI is one of these
				res.sendRedirect(loginpath);
				return;
			}
		} else if (URI.equals(loginpath) || URI.equals(basePath)) { // if already logged, redirect to the homepage
			res.sendRedirect(basePath + "Home");
			return;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	public void destroy() {
	}

}
