package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.primaryKeys.CancelledQuestionnairePK;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cancelled_questionnaires", schema = "db_gma")
@NamedQuery(name = "CancelledQuestionnaire.findCancelledQuestionnairesForQuestionnaire", query = "SELECT cq FROM CancelledQuestionnaire cq WHERE cq.questionnaire.id = ?1 ORDER BY cq.log_timestamp DESC")
public class CancelledQuestionnaire {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CancelledQuestionnairePK id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "iduser", referencedColumnName = "iduser", nullable = false)
	private User user;

	@ManyToOne
	@MapsId("questionnaireId")
	@JoinColumn(name = "idquestionnaire", referencedColumnName = "idquestionnaire", nullable = false)
	private Questionnaire questionnaire;

	@Temporal(TemporalType.TIMESTAMP)
	private Date log_timestamp;

	public CancelledQuestionnaire(Questionnaire questionnaire, User user, Date log_timestamp) {
		this.id = new CancelledQuestionnairePK(user.getId(), questionnaire.getId());
		this.questionnaire = questionnaire;
		this.user = user;
		this.log_timestamp = log_timestamp;
		questionnaire.addCancelledQuestionnaire(this);
	}

	public CancelledQuestionnaire() {
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

	public Date getLog_timestamp() {
		return log_timestamp;
	}

	public void setLog_timestamp(Date logTimestamp) {
		this.log_timestamp = logTimestamp;
	}
}
