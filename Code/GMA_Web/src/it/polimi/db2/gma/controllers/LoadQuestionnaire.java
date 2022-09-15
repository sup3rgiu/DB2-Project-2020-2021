package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.exceptions.QuestionnaireException;
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

@WebServlet("/LoadQuestionnaire")
public class LoadQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;

	public LoadQuestionnaire() {
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
		User user = (User) session.getAttribute("user");

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		Questionnaire questionnaire = null;
		try {
			questionnaire = qService.getQuestionnaireOfTheDay();
			if (questionnaire == null) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND, "No questionnaire found for today!");
				Utils.processError(ctx, templateEngine, "No questionnaire found for today!");
				return;
			}
			if (questionnaire.getQuestions().isEmpty()) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND, "Malformed questionnaire. No questions found");
				Utils.processError(ctx, templateEngine, "Malformed questionnaire. No questions found");
				return;
			}
			if (qService.questionnaireAlreadySubmittedByUser(questionnaire, user)) {
				//response.sendError(HttpServletResponse.SC_FORBIDDEN, "You've already submitted this questionnaire"); // server side check if the user tries to bypass the HTML
				Utils.processError(ctx, templateEngine, "You've already submitted this questionnaire");
				return;
			}
		} catch (Exception e) {
			// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover questionnaire");
			Utils.processError(ctx, templateEngine, "Not possible to recover questionnaire");
			return;
		}

		try {
			qService.logQuestionnaireAccess(questionnaire, user);
		} catch (QuestionnaireException e) {
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to log the questionnaire access");
			Utils.processError(ctx, templateEngine, "Not possible to log the questionnaire access");
			return;
		}

		String path = "/WEB-INF/Questionnaire.html";
		ctx.setVariable("questionnaire", questionnaire);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
	}

}
