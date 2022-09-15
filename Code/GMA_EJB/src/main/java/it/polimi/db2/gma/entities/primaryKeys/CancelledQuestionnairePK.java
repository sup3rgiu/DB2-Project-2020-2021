package it.polimi.db2.gma.entities.primaryKeys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CancelledQuestionnairePK implements Serializable {

	@Column(name = "iduser")
	private int userId;

	@Column(name = "idquestionnaire")
	private int questionnaireId;

	public CancelledQuestionnairePK() {
	}

	public CancelledQuestionnairePK(int questionnaireId, int userId) {
		this.questionnaireId = questionnaireId;
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public int getQuestionnaireId() {
		return questionnaireId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CancelledQuestionnairePK that = (CancelledQuestionnairePK) o;
		return questionnaireId == that.questionnaireId && userId == that.userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(questionnaireId, userId);
	}
}
