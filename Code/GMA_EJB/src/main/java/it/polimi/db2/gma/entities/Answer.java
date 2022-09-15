package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.primaryKeys.AnswerPK;

import javax.persistence.*;

@Entity
@Table(name = "answers", schema = "db_gma")
@NamedQueries({
		@NamedQuery(name = "Answer.findAnswersForQuestionnaireByUser", query = "SELECT a FROM Answer a WHERE a.questionnaire.id = ?1 and a.user.id = ?2"),
		@NamedQuery(name = "Answer.findUsersWhoAnsweredQuestionnaire", query = "SELECT DISTINCT a.user FROM Answer a WHERE a.questionnaire.id = ?1"),
})
public class Answer {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AnswerPK id;

	@ManyToOne
	@MapsId("questionnaireId")
	@JoinColumn(name = "idquestionnaire", referencedColumnName = "idquestionnaire", nullable = false, insertable = false, updatable = false)
	private Questionnaire questionnaire;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "iduser", referencedColumnName = "iduser", nullable = false)
	private User user;

	@MapsId("questionNumber")
	@Column(name = "question_number", nullable = false, insertable = false, updatable = false)
	private int questionNumber;

	private String answer;

	// only for support. Remove if not needed. If removed, remove also "insertable = false, updatable = false" from idquestionnaire and question_number
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumns({
			@JoinColumn(name = "idquestionnaire", referencedColumnName = "idquestionnaire"),
			@JoinColumn(name = "question_number", referencedColumnName = "question_number")
	})
	private Question question;

	public Answer() {
	}

	public Answer(Questionnaire questionnaire, User user, int questionNumber, String answer) {
		this.id = new AnswerPK(questionnaire.getId(), user.getId(), questionNumber);
		this.questionnaire = questionnaire;
		this.user = user;
		this.questionNumber = questionNumber;
		this.answer = answer;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
}
