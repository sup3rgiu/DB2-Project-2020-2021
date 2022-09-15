package it.polimi.db2.gma.controllers.admin;

import it.polimi.db2.gma.controllers.Utils;
import it.polimi.db2.gma.entities.*;
import it.polimi.db2.gma.exceptions.InvalidQuestionnaireParameterException;
import it.polimi.db2.gma.exceptions.QuestionException;
import it.polimi.db2.gma.exceptions.QuestionnaireException;
import it.polimi.db2.gma.services.ProductService;
import it.polimi.db2.gma.services.QuestionService;
import it.polimi.db2.gma.services.QuestionnaireService;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

@WebServlet("/admin/CreateQuestionnaire")
public class CreateQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService pService;
	@EJB(name = "it.polimi.db2.gma.services/QuestionService")
	private QuestionService quService;


	public CreateQuestionnaire() {
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

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		Map<String, String[]> parameters = request.getParameterMap();
		Questionnaire questionnaire = null;
		try {
			questionnaire = createQuestionnaire(parameters);
		} catch (InvalidQuestionnaireParameterException e) {
			//response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			Utils.processError(ctx, templateEngine, e.getMessage());
			return;
		} catch (QuestionException e) {
			Utils.processError(ctx, templateEngine, "Failed to create the questionnaire");
			return;
		}

		String path = "/WEB-INF/Admin.html";
		ctx.setVariable("createdQuestionnaire", questionnaire);
		templateEngine.process(path, ctx, response.getWriter());
	}

	private Questionnaire createQuestionnaire(Map<String, String[]> parameters) throws InvalidQuestionnaireParameterException, QuestionException {
		boolean isValidQuestionnaire = false;
		isValidQuestionnaire = validateQuestionnaire(parameters);
		if (!isValidQuestionnaire) {
			throw new InvalidQuestionnaireParameterException("Malformed questionnaire");
		}

		int numOfQuestions = Integer.parseInt(parameters.get("numberOfQuestions")[0]);

		List<Question> questions = new ArrayList<>();

		for (int i = 0; i++ < numOfQuestions; ) { // start from 1 up to numOfQuestions
			String questionKey = "question[" + i + "][question]";
			String typeKey = "question[" + i + "][type]";
			String optionsKey = "question[" + i + "][options]";

			String questionText = parameters.get(questionKey)[0];
			InputTypeValue type_value = InputTypeValue.valueOf(parameters.get(typeKey)[0].toLowerCase());
			List<String> options = null;

			if (type_value == InputTypeValue.select || (type_value == InputTypeValue.number && parameters.containsKey(optionsKey)))
				options = Arrays.asList(parameters.get(optionsKey));

			InputType inputType = quService.getInputTypeByValue(type_value);

			List<OptionChoice> choices = new ArrayList<>();
			OptionGroup optionGroup = null;
			if (options != null && !options.isEmpty()) {
				options.forEach(option -> choices.add(new OptionChoice(option)));
				optionGroup = new OptionGroup(choices); // create OptionGroup
			}

			Question question = new Question(questionText, inputType, optionGroup);
			questions.add(question);
		}

		Product product = pService.findProductId(Integer.parseInt(parameters.get("productId")[0]));
		Date date = Utils.dateFromString(parameters.get("day")[0]);
		Questionnaire questionnaire = qService.createQuestionnaire(product, date, questions);

		return questionnaire;
	}

	private boolean validateQuestionnaire(Map<String, String[]> parameters) throws InvalidQuestionnaireParameterException {
		if (!parameters.containsKey("productId") || !StringUtils.isNumeric(parameters.get("productId")[0]) || pService.findProductId(Integer.parseInt(parameters.get("productId")[0])) == null) {
			throw new InvalidQuestionnaireParameterException("Product missing or invalid");
			//return false;
		}

		if (!parameters.containsKey("day") || !Utils.isValidDate(parameters.get("day")[0]) || !Utils.isDateGraterThanYesterday(Utils.dateFromString(parameters.get("day")[0]))) {
			throw new InvalidQuestionnaireParameterException("Date missing or invalid");
			//return false;
		}

		Date day = Utils.dateFromString(parameters.get("day")[0]);
		try {
			if (qService.getQuestionnaireByDay(day) != null) {
				throw new InvalidQuestionnaireParameterException("There is already a Questionnaire for the day: " + parameters.get("day")[0]);
				// return false;
			}
		} catch (QuestionnaireException e) {
			e.printStackTrace();
		}

		if (!parameters.containsKey("numberOfQuestions") || !StringUtils.isNumeric(parameters.get("numberOfQuestions")[0]) || Integer.parseInt(parameters.get("numberOfQuestions")[0]) < 1) {
			throw new InvalidQuestionnaireParameterException("Malformed questionnaire");
			//return false;
		}

		int numOfQuestions = Integer.parseInt(parameters.get("numberOfQuestions")[0]);

		for (int i = 0; i++ < numOfQuestions; ) { // start from 1 up to numOfQuestions
			String questionKey = "question[" + i + "][question]";
			String typeKey = "question[" + i + "][type]";
			String optionsKey = "question[" + i + "][options]";

			if (!parameters.containsKey(questionKey) || StringUtils.isBlank(parameters.get(questionKey)[0])) {
				throw new InvalidQuestionnaireParameterException("Malformed questionnaire. Missing question for question number " + i);
				// return false;
			}
			if (!parameters.containsKey(typeKey) || !Utils.isInEnum(parameters.get(typeKey)[0].toLowerCase(), InputTypeValue.class)) {
				throw new InvalidQuestionnaireParameterException("Invalid Answer Input Type: " + parameters.get(typeKey)[0]);
				// return false;
			}

			InputTypeValue type = InputTypeValue.valueOf(parameters.get(typeKey)[0].toLowerCase());

			if (type == InputTypeValue.select) {
				if (!parameters.containsKey(optionsKey) || parameters.get(optionsKey).length < 2)
					throw new InvalidQuestionnaireParameterException("Missing selection Options or too few (min 2) for question: \"" + parameters.get(questionKey)[0] + "\"");
				// return false;
			} else if (type == InputTypeValue.number) {
				if (parameters.containsKey(optionsKey)) {
					String[] options = parameters.get(optionsKey);
					if (options.length != 2) { // too many options (must be 2: MAX and MIN)
						throw new InvalidQuestionnaireParameterException("Too many options for question: \"" + parameters.get(questionKey)[0] + "\". Must be two options: MAX and MIN values");
						// return false;
					}
					for (String s : options) {
						if (!StringUtils.isNumeric(s)) { // options are not valid numbers
							throw new InvalidQuestionnaireParameterException("Invalid MAX and MIN values");
							// return false;
						}
					}
				}
			}
		}

		return true;
	}

	public void destroy() {
	}

}
