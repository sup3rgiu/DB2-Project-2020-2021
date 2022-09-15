package it.polimi.db2.gma.controllers.admin;

import it.polimi.db2.gma.controllers.Utils;
import it.polimi.db2.gma.entities.CancelledQuestionnaire;
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
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/InspectQuestionnaire")
public class InspectQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;

	public InspectQuestionnaire() {
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

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		Integer questionnaireId = null;
		try {
			questionnaireId = Integer.parseInt(request.getParameter("questionnaireid"));
		} catch (NumberFormatException | NullPointerException e) {
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			Utils.processError(ctx, templateEngine, "Incorrect param values");
			return;
		}

		List<User> usersWhoAnswered = null;
		Questionnaire questionnaire = null;
		try {
			questionnaire = qService.findQuestionnaireByIdRefresh(questionnaireId);
			if (questionnaire == null) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				Utils.processError(ctx, templateEngine, "Resource not found");
				return;
			}
			if(!Utils.isDateSmallerThanToday(questionnaire.getDate())) {
				Utils.processError(ctx, templateEngine, "You are trying to inspect a present or future questionnaire. Inspection is possibile only for past questionnaires");
				return;
			}
			usersWhoAnswered = qService.getUsersWhoAnsweredQuestionnaire(questionnaire);
		} catch (Exception e) {
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover questionnaire");
			Utils.processError(ctx, templateEngine, "Not possible to recover questionnaire");
			return;
		}

		String path = "/WEB-INF/InspectQuestionnaire.html";
		ctx.setVariable("questionnaire", questionnaire);
		ctx.setVariable("usersWhoAnswered", usersWhoAnswered);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
	}

}
