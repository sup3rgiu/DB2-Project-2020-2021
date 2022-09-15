package it.polimi.db2.gma.entities.primaryKeys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnswerPK implements Serializable {


	@Column(name = "idquestionnaire")
	private int questionnaireId;

	@Column(name = "iduser")
	private int userId;

	@Column(name = "question_number", insertable = false, updatable = false)
	private int questionNumber;

	public AnswerPK() {
	}

	public AnswerPK(int questionnaireId, int userId, int questionNumber) {
		this.questionnaireId = questionnaireId;
		this.userId = userId;
		this.questionNumber = questionNumber;
	}

	public int getQuestionnaireId() {
		return questionnaireId;
	}

	public int getUserId() {
		return userId;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnswerPK answerPK = (AnswerPK) o;
		return questionnaireId == answerPK.questionnaireId && userId == answerPK.userId && questionNumber == answerPK.questionNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(questionnaireId, userId, questionNumber);
	}
}
