package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.services.QuestionnaireService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/Home")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;

	public GoToHomePage() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		User user = (User) session.getAttribute("user");
		Questionnaire questionnaire = null;
		Product product = null;
		try {
			questionnaire = qService.getQuestionnaireOfTheDay();
			if (questionnaire != null) {
				if (questionnaire.getProduct() == null) {
					return; // something wrong. Questionnaire exists but not the associated product
				}
				product = questionnaire.getProduct();
			}
		} catch (Exception e) {
			Utils.processError(ctx, templateEngine, "Not possible to get data");
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get data");
			return;
		}

		// Redirect to the Home page and add the parameters
		String path = "/WEB-INF/Home.html";
		ctx.setVariable("product", product);
		ctx.setVariable("questionnaire", questionnaire);
		ctx.setVariable("questionnaireAlreadySubmitted", qService.questionnaireAlreadySubmittedByUser(questionnaire, user));

		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
