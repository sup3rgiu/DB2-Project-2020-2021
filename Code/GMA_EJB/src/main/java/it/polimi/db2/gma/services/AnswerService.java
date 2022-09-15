package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.Answer;
import it.polimi.db2.gma.entities.Question;
import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.entities.primaryKeys.QuestionPK;
import it.polimi.db2.gma.exceptions.AnswerException;
import it.polimi.db2.gma.exceptions.AnswerNotValidException;
import it.polimi.db2.gma.exceptions.QuestionNotFoundException;
import it.polimi.db2.gma.exceptions.QuestionnaireException;
import org.apache.commons.lang3.math.NumberUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Stateless
public class AnswerService {
	// For debugging: used to print the native transaction Id of MySQL
	@EJB(name = "it.polimi.db2.gma.services/MySqlTxUtils")
	private MySqlTxUtils util;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public AnswerService() {
	}

	public void addAnswers(Questionnaire questionnaire, User user, List<Answer> answers) throws AnswerException {
		for (Answer answer : answers) {
			Question question = questionnaire.getQuestions().stream().filter(q -> q.getQuestionNumber() == answer.getQuestionNumber()).findFirst().orElse(null);
			if (question != null)
				question.addAnswer(answer);
			else
				throw new AnswerException("Question not found for this answer");
		}

		em.merge(questionnaire);

		try {
			qService.removeQuestionnaireLog(questionnaire, user); // remove log if user previously canceled this questionnaire
		} catch (QuestionnaireException e) {
			e.printStackTrace();
		}

		//em.flush();
	}

	public Answer createAnswer(Questionnaire questionnaire, User user, int questionNumber, String answer) throws QuestionNotFoundException, AnswerNotValidException {
		if (validateAnswer(questionnaire, questionNumber, answer))
			return new Answer(questionnaire, user, questionNumber, answer);
		else
			throw new AnswerNotValidException("Invalid answer");
	}

	private boolean validateAnswer(Questionnaire questionnaire, int questionNumber, String answer) throws QuestionNotFoundException {
		if (answer == null || answer.isBlank()) return false;
		Question question = em.find(Question.class, new QuestionPK(questionnaire.getId(), questionNumber));
		if (question == null) {
			throw new QuestionNotFoundException("Question not found");
		}

		switch (question.getInputType().getType()) {
			case text:
			case textarea:
				return true;

			case number:
				if (!NumberUtils.isCreatable(answer)) return false;
				if (question.getOptionGroup() != null)
					return Integer.parseInt(answer) >= question.getOptionGroup().getMinChoice() && Integer.parseInt(answer) <= question.getOptionGroup().getMaxChoice();
				return true;

			case select:
				return question.getOptionGroup().getChoices().stream().anyMatch(choice -> choice.getChoice().equals(answer));

			default:
				return false;
		}
	}

	public List<Answer> getAnswersForQuestionnaireByUser(Questionnaire questionnaire, User user) throws AnswerException {
		List<Answer> aList = null;
		try {
			aList = em.createNamedQuery("Answer.findAnswersForQuestionnaireByUser", Answer.class).setParameter(1, questionnaire.getId()).setParameter(2, user.getId())
					.getResultList();
		} catch (PersistenceException e) {
			throw new AnswerException("Could not load answers for this <questionnaire, user>");
		}

		if (aList == null || aList.isEmpty())
			return null;

		return aList;
	}
}