package it.polimi.db2.gma.controllers.admin;

import it.polimi.db2.gma.controllers.Utils;
import it.polimi.db2.gma.entities.Questionnaire;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin/DeleteQuestionnaire")
public class DeleteQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;

	public DeleteQuestionnaire() {
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
			Utils.processError(ctx, templateEngine, "Invalid param values");
			return;
		}

		Questionnaire questionnaire = null;
		try {
			questionnaire = qService.findQuestionnaireByIdRefresh(questionnaireId);
			if (questionnaire == null) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				Utils.processError(ctx, templateEngine, "Resource not found");
				return;
			}
			if(!Utils.isDateSmallerThanToday(questionnaire.getDate())) {
				Utils.processError(ctx, templateEngine, "You are trying to delete a present or future questionnaire. Deletion is possibile only for past questionnaires");
				return;
			}
			qService.deleteQuestionnaire(questionnaire);
		} catch (Exception e) {
			// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover questionnaire");
			Utils.processError(ctx, templateEngine, "Not possible to recover questionnaire");
			return;
		}

		// pass information about the deleted questionnaire to show them in the frontend
		StringBuilder queryString = new StringBuilder();
		String deletedQuestProduct = questionnaire.getProduct().getName();
		String deletedQuestDate = questionnaire.getDateString();
		queryString.append("deletedQuestionnaireProduct=").append(URLEncoder.encode(deletedQuestProduct, StandardCharsets.UTF_8)).append("&");
		queryString.append("deletedQuestionnaireDate=").append(URLEncoder.encode(deletedQuestDate, StandardCharsets.UTF_8)).append("&");
		String path = getServletContext().getContextPath() + "/admin/Deletion?" + queryString;
		response.sendRedirect(path);
	}

	public void destroy() {
	}

}
