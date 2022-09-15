package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.primaryKeys.LeaderboardPK;

import javax.persistence.*;

@Entity
@Table(name = "leaderboard", schema = "db_gma")
public class Leaderboard {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LeaderboardPK id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "iduser", referencedColumnName = "iduser", nullable = false)
	private User user;

	@ManyToOne
	@MapsId("questionnaireId")
	@JoinColumn(name = "idquestionnaire", referencedColumnName = "idquestionnaire", nullable = false)
	private Questionnaire questionnaire;

	private int points;

	public Leaderboard() {
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}
