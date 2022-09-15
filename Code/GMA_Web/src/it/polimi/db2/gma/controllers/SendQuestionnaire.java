package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.Answer;
import it.polimi.db2.gma.entities.Question;
import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.exceptions.*;
import it.polimi.db2.gma.services.AnswerService;
import it.polimi.db2.gma.services.OffensiveWordService;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/SendQuestionnaire")
public class SendQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.polimi.db2.gma.services/AnswerService")
	private AnswerService aService;
	@EJB(name = "it.polimi.db2.gma.services/OffensiveWordService")
	private OffensiveWordService owService;
	@EJB(name = "it.polimi.db2.gma.services/UserService")
	private UserService usrService;

	int offensiveWordsCheckMethod = 2; // 1 to do the check in memory through Java, 2 to do the check through SQL

	public SendQuestionnaire() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		Integer questionnaireId = null;
		try {
			questionnaireId = Integer.parseInt(request.getParameter("questionnaireid"));
		} catch (NumberFormatException | NullPointerException e) {
			// response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			Utils.processError(ctx, templateEngine, "Incorrect param values");
			return;
		}

		Questionnaire questionnaire = null;
		List<Question> questions = null;
		Set<String> offensiveWordSet = null;
		try {
			questionnaire = qService.findQuestionnaireById(questionnaireId);

			if (questionnaire == null || questionnaire.getQuestions().isEmpty()) {
				//response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				Utils.processError(ctx, templateEngine, "Resource not found");
				return;
			}

			if (qService.questionnaireAlreadySubmittedByUser(questionnaire, user)) {
				//response.sendError(HttpServletResponse.SC_FORBIDDEN, "You've already submitted this questionnaire"); // server side check if the user tries to bypass the HTML
				Utils.processError(ctx, templateEngine, "You've already submitted this questionnaire");
				return;
			}

			questions = questionnaire.getQuestions();
			if (offensiveWordsCheckMethod == 1) offensiveWordSet = owService.getOffensiveWords();
		} catch (Exception e) {
			// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover questionnaire");
			Utils.processError(ctx, templateEngine, "Not possible to recover questionnaire");
			return;
		}

		Map<String, String[]> parameters = request.getParameterMap();
		List<Answer> answerList = new ArrayList<>();
		for (Question q : questions) {
			String answerText = parameters.get(Integer.toString(q.getQuestionNumber()))[0]; // TODO if needed, handle parameters with multiple values as response (such as multiple checkbox)
			boolean answerIsPresent = (answerText != null);
			if (!answerIsPresent && !q.isStatistical()) {
				//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing mandatory answer");
				Utils.processError(ctx, templateEngine, "Missing mandatory answer");
				return;
			}

			if (answerIsPresent) {
				if (q.isStatistical() && answerText.isBlank()) // skip optional question if answer is empty (i.e.: not answered)
					continue;

				if (containsOffensiveWords(answerText, offensiveWordSet)) { // block user
					try {
						usrService.banUser(user);
					} catch (UpdateProfileException e) {
						// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to ban user");
						return;
					}
					String path = getServletContext().getContextPath() + "/Home";
					response.sendRedirect(path);
					return;
				}

				Answer answer = null;
				try {
					answer = aService.createAnswer(questionnaire, user, q.getQuestionNumber(), answerText);
				} catch (QuestionNotFoundException e) {
					//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Something wrong");
					Utils.processError(ctx, templateEngine, "Something wrong");
					return;
				} catch (AnswerNotValidException e) {
					//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid answer for question: " + q.getText());
					Utils.processError(ctx, templateEngine, "Invalid answer for question: " + q.getText());
					return;
				}
				answerList.add(answer);
			}
		}


		if (!answerList.isEmpty()) {
			try {
				aService.addAnswers(questionnaire, user, answerList);
			} catch (AnswerException e) {
				//response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Something wrong");
				Utils.processError(ctx, templateEngine, "Something wrong");
				return;
			}
		}

		// Redirect to the Thanks page
		String path = "/WEB-INF/Thanks.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	private boolean containsOffensiveWords(String answer, Set<String> offensiveWords) {
		if (offensiveWordsCheckMethod == 1) { // to use if offensiveWords.size() is relative small
			Set<String> answerSet = Arrays.stream(answer.replaceAll("\\p{P}", "").toLowerCase().split("\\s+")).collect(Collectors.toSet()); // remove punctuation, convert to lowercase, split the string w.r.t spaces
			return !Collections.disjoint(answerSet, offensiveWords);
		} else if (offensiveWordsCheckMethod == 2) { // to use if offensiveWords.size() is huge
			try {
				return owService.containsOffensiveWord(answer); // check done through SQL query
			} catch (OffensiveWordException e) {
				return false;
			}
		}

		return false;
	}

	public void destroy() {
	}

}
