package it.polimi.db2.gma.entities.primaryKeys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionPK implements Serializable {

	@Column(name = "idquestionnaire")
	private int questionnaireId;

	@Column(name = "question_number")
	private int questionNumber;

	public QuestionPK() {
	}

	public QuestionPK(int questionnaireId, int questionNumber) {
		this.questionnaireId = questionnaireId;
		this.questionNumber = questionNumber;
	}

	public int getQuestionnaireId() {
		return questionnaireId;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		QuestionPK that = (QuestionPK) o;
		return questionnaireId == that.questionnaireId && questionNumber == that.questionNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(questionnaireId, questionNumber);
	}
}
