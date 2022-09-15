package it.polimi.db2.gma.entities.primaryKeys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LeaderboardPK implements Serializable {

	@Column(name = "iduser")
	private int userId;

	@Column(name = "idquestionnaire")
	private int questionnaireId;

	public LeaderboardPK() {
	}

	public LeaderboardPK(int userId, int questionnaireId) {
		this.userId = userId;
		this.questionnaireId = questionnaireId;
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
		LeaderboardPK that = (LeaderboardPK) o;
		return questionnaireId == that.questionnaireId && userId == that.userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(questionnaireId, userId);
	}
}
