package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.services.UserService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet("/Signup")
@MultipartConfig
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/UserService")
	private UserService uService;

	public SignUp() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// obtain and escape params
		String usrn, email, pwd, confirm_pwd;
		usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
		confirm_pwd = StringEscapeUtils.escapeJava(request.getParameter("confirm_pwd"));

		// check params integrity
		if (usrn == null || email == null || pwd == null || confirm_pwd == null || usrn.isEmpty() || email.isEmpty() || pwd.isEmpty() || confirm_pwd.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Credentials must be not null");
			return;
		}

		if (!pwd.equals(confirm_pwd)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Passwords don't match");
			return;
		}

		final String email_regex = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		final Pattern pattern = Pattern.compile(email_regex);
		if (!pattern.matcher(email).matches()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is not valid");
			return;
		}

		// query db to check if this username or email is already in use
		boolean emailExists = false;
		boolean userExists = false;
		try {
			emailExists = uService.checkEmailExists(email);
			userExists = uService.checkUsernameExists(usrn);
		} catch (Exception e) {
			return;
		}

		User user = null;
		if (!emailExists && !userExists) {
			user = uService.createUser(usrn, pwd, email);
		}

		String path;
		if (user == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

			// add one error only
			if (emailExists) {
				ctx.setVariable("signupErrorMsg", "Email already exists");
			} else if (userExists) {
				ctx.setVariable("signupErrorMsg", "Username already exists");
			}

			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}

	public void destroy() {
	}
}
