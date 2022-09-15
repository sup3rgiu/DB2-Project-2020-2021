package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.exceptions.CredentialsException;
import it.polimi.db2.gma.exceptions.UpdateProfileException;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class UserService {
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public UserService() {
	}

	public User createUser(String username, String password, String email) {
		User u = new User();
		u.setUsername(username);
		u.setPassword(password);
		u.setEmail(email);
		em.persist(u);
		return u;
	}

	public boolean checkUsernameExists(String username) throws CredentialsException {
		User user = null;
		try {
			user = em.createNamedQuery("User.findUserByUsername", User.class).setParameter(1, username)
					.getSingleResult();
		} catch (NoResultException e) {
			return false;
		} catch (PersistenceException e) {
			throw new CredentialsException("Could not verify user existence");
		}
		return true;
	}

	public boolean checkEmailExists(String email) throws CredentialsException {
		User user = null;
		try {
			user = em.createNamedQuery("User.findUserByEmail", User.class).setParameter(1, email)
					.getSingleResult();
		} catch (NoResultException e) {
			return false;
		} catch (PersistenceException e) {
			throw new CredentialsException("Could not verify user existence");
		}
		return true;
	}

	public User checkCredentials(String usrn, String pwd) throws CredentialsException, NonUniqueResultException {
		List<User> uList = null;
		try {
			uList = em.createNamedQuery("User.checkCredentials", User.class).setParameter(1, usrn).setParameter(2, pwd)
					.getResultList();
		} catch (PersistenceException e) {
			throw new CredentialsException("Could not verify credentials");
		}
		if (uList.isEmpty())
			return null;
		else if (uList.size() == 1)
			return uList.get(0);
		throw new NonUniqueResultException("More than one user registered with same credentials");
	}

	public void banUser(User u) throws UpdateProfileException {
		if (u.isAdmin()) {
			throw new UpdateProfileException("Could not ban an Admin user");
		}

		u.setBlocked(true);
		try {
			em.merge(u);
		} catch (PersistenceException e) {
			throw new UpdateProfileException("Could not ban user");
		}
	}

	public User findUserById(int userId) {
		User user = em.find(User.class, userId);
		return user;
	}
}
