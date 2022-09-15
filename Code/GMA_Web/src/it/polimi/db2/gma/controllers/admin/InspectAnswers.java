package it.polimi.db2.gma.controllers.admin;

import it.polimi.db2.gma.controllers.Utils;
import it.polimi.db2.gma.entities.Answer;
import it.polimi.db2.gma.entities.Question;
import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.services.AnswerService;
import it.polimi.db2.gma.services.QuestionnaireService;
import it.polimi.db2.gma.services.UserService;
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
import java.util.LinkedHashMap;
import java.util.List;

@WebServlet("/admin/InspectAnswers")
public class InspectAnswers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.polimi.db2.gma.services/UserService")
	private UserService uService;
	@EJB(name = "it.polimi.db2.gma.services/AnswerService")
	private AnswerService aService;

	public InspectAnswers() {
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
		Integer userId = null;
		try {
			questionnaireId = Integer.parseInt(request.getParameter("questionnaireid"));
			userId = Integer.parseInt(request.getParameter("userid"));
		} catch (NumberFormatException | NullPointerException e) {
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			Utils.processError(ctx, templateEngine, "Incorrect param values");
			return;
		}

		List<Answer> answers = null;
		List<Question> questions = null;
		Questionnaire questionnaire = null;
		User userToInspect = null;
		try {
			questionnaire = qService.findQuestionnaireById(questionnaireId);
			userToInspect = uService.findUserById(userId);
			if (questionnaire == null || userToInspect == null) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				Utils.processError(ctx, templateEngine, "Resource not found");
				return;
			}
			if(!Utils.isDateSmallerThanToday(questionnaire.getDate())) {
				Utils.processError(ctx, templateEngine, "You are trying to inspect a present or future questionnaire. Inspection is possibile only for past questionnaires");
				return;
			}
			answers = aService.getAnswersForQuestionnaireByUser(questionnaire, userToInspect);
			questions = questionnaire.getQuestions();
		} catch (Exception e) {
			// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover questionnaire");
			Utils.processError(ctx, templateEngine, "Not possible to recover questionnaire");
			return;
		}

		LinkedHashMap<Question, Answer> questionAnswersMap = new LinkedHashMap<>();
		for (Question q : questions) {
			Answer answerForQuestion = answers.stream().filter(a -> a.getQuestion().equals(q)).findFirst().orElse(null);
			questionAnswersMap.put(q, answerForQuestion);
		}

		String path = "/WEB-INF/LoadAnswers.html";
		ctx.setVariable("questionnaire", questionnaire);
		ctx.setVariable("userToInspect", userToInspect);
		ctx.setVariable("questionAnswersMap", questionAnswersMap);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
	}

}
