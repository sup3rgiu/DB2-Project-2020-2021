package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.OffensiveWord;
import it.polimi.db2.gma.exceptions.OffensiveWordException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class OffensiveWordService {
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;
	@EJB(name = "it.polimi.db2.gma.services/MySqlTxUtils")
	private MySqlTxUtils util;

	public OffensiveWordService() {
	}

	public Set<String> getOffensiveWords() throws OffensiveWordException {
		Set<String> offensiveWords = null;
		try {
			offensiveWords = em.createNamedQuery("OffensiveWord.getOffensiveWords", OffensiveWord.class)
					.getResultStream().map(OffensiveWord::getWord).collect(Collectors.toSet());
		} catch (PersistenceException e) {
			throw new OffensiveWordException("Could not load offensive words");
		}

		return offensiveWords;
	}

	public boolean containsOffensiveWord(String text) throws OffensiveWordException {
		List<OffensiveWord> offensiveWords = null;
		try {
			offensiveWords = em.createNamedQuery("OffensiveWord.getBannedWordsInString", OffensiveWord.class).setParameter(1, text)
					.getResultList();
		} catch (PersistenceException e) {
			throw new OffensiveWordException("Could not check if string contains offensive words");
		}

		return !offensiveWords.isEmpty();
	}

}
