package it.polimi.db2.gma.exceptions;

public class QuestionNotFoundException extends QuestionException {
	private static final long serialVersionUID = 1L;

	public QuestionNotFoundException(String message) {
		super(message);
	}
}
