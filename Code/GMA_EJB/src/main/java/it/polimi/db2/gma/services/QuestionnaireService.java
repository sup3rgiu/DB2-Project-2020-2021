package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.*;
import it.polimi.db2.gma.entities.primaryKeys.CancelledQuestionnairePK;
import it.polimi.db2.gma.exceptions.AnswerException;
import it.polimi.db2.gma.exceptions.QuestionException;
import it.polimi.db2.gma.exceptions.QuestionnaireException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

@Stateless
public class QuestionnaireService {
	// For debugging: used to print the native transaction Id of MySQL
	@EJB(name = "it.polimi.db2.gma.services/MySqlTxUtils")
	private MySqlTxUtils util;
	@EJB(name = "it.polimi.db2.gma.services/AnswerService")
	private AnswerService aService;
	@EJB(name = "it.polimi.db2.gma.services/QuestionService")
	private QuestionService qService;
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public QuestionnaireService() {
	}

	public Questionnaire getQuestionnaireOfTheDay() throws QuestionnaireException {
		Questionnaire questionnaire = null;
		try {
			questionnaire = em.createNamedQuery("Questionnaire.findQuestionnaireOfTheDay", Questionnaire.class).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException e) {
			throw new QuestionnaireException("Cannot load questionnaire");
		}
		return questionnaire;
	}

	public Questionnaire getQuestionnaireOfTheDayRefresh() throws QuestionnaireException {
		Questionnaire questionnaire = getQuestionnaireOfTheDay();
		em.refresh(questionnaire); // needed to refresh the leaderboard since it's updated with a db trigger
		return questionnaire;
	}

	public Questionnaire findQuestionnaireById(int questionnaireId) {
		Questionnaire questionnaire = em.find(Questionnaire.class, questionnaireId);
		return questionnaire;
	}

	public Questionnaire findQuestionnaireByIdRefresh(int questionnaireId) {
		Questionnaire questionnaire = em.find(Questionnaire.class, questionnaireId);
		em.refresh(questionnaire);
		return questionnaire;
	}

	public void logQuestionnaireAccess(Questionnaire questionnaire, User user) throws QuestionnaireException {
		CancelledQuestionnaire cQuestionnaire = null;
		cQuestionnaire = em.find(CancelledQuestionnaire.class, new CancelledQuestionnairePK(questionnaire.getId(), user.getId()));

		if (cQuestionnaire != null) {
			cQuestionnaire.setLog_timestamp(new Date());
			try {
				em.merge(cQuestionnaire);
			} catch (PersistenceException e) {
				throw new QuestionnaireException("Could not update timestamp of cancelled questionnaire");
			}
		} else {
			cQuestionnaire = new CancelledQuestionnaire(questionnaire, user, new Date());
			em.persist(cQuestionnaire);
		}

		em.flush();
	}

	public void removeQuestionnaireLog(Questionnaire questionnaire, User user) throws QuestionnaireException {
		CancelledQuestionnaire cQuestionnaire = em.find(CancelledQuestionnaire.class, new CancelledQuestionnairePK(questionnaire.getId(), user.getId()));

		if (cQuestionnaire != null) {
			try {
				em.remove(cQuestionnaire);
			} catch (PersistenceException e) {
				throw new QuestionnaireException("Could not delete canceled questionnaire log");
			}
		}
	}

	public boolean questionnaireAlreadySubmittedByUser(Questionnaire questionnaire, User user) {
		if (questionnaire == null) return false;
		try {
			return aService.getAnswersForQuestionnaireByUser(questionnaire, user) != null;
		} catch (AnswerException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Questionnaire createQuestionnaire(Product product, Date date, List<Question> questions) throws QuestionException {
		List<FixedQuestion> fixedQuestions = qService.getAllFixedQuestions();
		if (fixedQuestions != null) { // add fixed questions to the questionnaire
			fixedQuestions.forEach(fixedQuestion -> {
				Question question = new Question(fixedQuestion.getText(), fixedQuestion.getInputType(), fixedQuestion.getOptionGroup());
				question.setStatistical(true);
				questions.add(question);
			});
		}

		Questionnaire questionnaire = new Questionnaire(product, date, questions);
		em.persist(questionnaire);
		em.flush();
		return questionnaire;
	}

	public Questionnaire getQuestionnaireByDay(Date day) throws QuestionnaireException {
		Questionnaire questionnaire = null;
		try {
			questionnaire = em.createNamedQuery("Questionnaire.findQuestionnaireByDay", Questionnaire.class).setParameter(1, day)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (PersistenceException e) {
			throw new QuestionnaireException("Cannot load questionnaire");
		}
		return questionnaire;
	}

	public List<Questionnaire> getAllPastQuestionnaires() throws QuestionnaireException {
		List<Questionnaire> qList = null;
		try {
			qList = em.createNamedQuery("Questionnaire.findAllPastQuestionnaires", Questionnaire.class)
					.getResultList();
		} catch (PersistenceException e) {
			throw new QuestionnaireException("Could not load past questionnaires");
		}

		if (qList.isEmpty())
			return null;

		return qList;
	}

	public List<User> getUsersWhoAnsweredQuestionnaire(Questionnaire questionnaire) throws QuestionnaireException {
		List<User> uList = null;
		try {
			uList = em.createNamedQuery("Answer.findUsersWhoAnsweredQuestionnaire", User.class).setParameter(1, questionnaire.getId())
					.getResultList();
		} catch (PersistenceException e) {
			throw new QuestionnaireException("Could not load user who answered this questionnaire");
		}

		if (uList.isEmpty())
			return null;

		return uList;
	}

	public Questionnaire deleteQuestionnaire(Questionnaire quest) {
		Questionnaire questionnaire = em.find(Questionnaire.class, quest.getId());
		em.remove(questionnaire);
		em.flush();
		em.getEntityManagerFactory().getCache().evict(Leaderboard.class); // invalidate cache of Leaderboard entities to refresh them after SQL trigger
		return questionnaire;
	}

}