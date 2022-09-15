package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.FixedQuestion;
import it.polimi.db2.gma.entities.InputType;
import it.polimi.db2.gma.entities.InputTypeValue;
import it.polimi.db2.gma.exceptions.QuestionException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Stateless
public class QuestionService {
	// For debugging: used to print the native transaction Id of MySQL
	@EJB(name = "it.polimi.db2.gma.services/MySqlTxUtils")
	private MySqlTxUtils util;
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public QuestionService() {
	}

	public InputType getInputTypeByValue(InputTypeValue typeValue) {
		List<InputType> iList = null;
		try {
			iList = em.createNamedQuery("InputType.getInputTypeByValue", InputType.class).setParameter(1, typeValue)
					.getResultList();
		} catch (PersistenceException e) {
			return null;
		}
		if (iList.isEmpty())
			return null;
		else if (iList.size() == 1)
			return iList.get(0);
		throw new NonUniqueResultException("More than one Input Types with this value");
	}

	public List<FixedQuestion> getAllFixedQuestions() throws QuestionException {
		List<FixedQuestion> fList = null;
		try {
			fList = em.createNamedQuery("FixedQuestion.getAllFixedQuestions", FixedQuestion.class)
					.getResultList();
		} catch (PersistenceException e) {
			throw new QuestionException("Could not fixed questions");
		}

		if (fList == null || fList.isEmpty())
			return null;

		return fList;
	}
}