package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.primaryKeys.QuestionPK;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "questions", schema = "db_gma")
public class Question {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuestionPK id;

	@ManyToOne
	@MapsId("questionnaireId")
	@JoinColumn(name = "idquestionnaire", referencedColumnName = "idquestionnaire", nullable = false)
	private Questionnaire questionnaire;

	@MapsId("questionNumber")
	@Column(name = "question_number", nullable = false, insertable = false, updatable = false)
	private int questionNumber;

	private String text;

	@Column(name = "is_statistical")
	private boolean isStatistical;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "option_group_id", referencedColumnName = "idoption")
	private OptionGroup optionGroup;

	@ManyToOne
	@JoinColumn(name = "input_type_id", referencedColumnName = "idinput", nullable = false)
	private InputType inputType;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.MERGE}, orphanRemoval = true)
	@JoinColumns({
			@JoinColumn(name = "idquestionnaire", referencedColumnName = "idquestionnaire", insertable = false, updatable = false),
			@JoinColumn(name = "question_number", referencedColumnName = "question_number", insertable = false, updatable = false),
	})
	private Set<Answer> answers = new HashSet<>();

	public Question() {
	}

	public Question(String text, InputType inputType, OptionGroup optionGroup) {
		this.text = text;
		this.inputType = inputType;
		this.optionGroup = optionGroup;
		this.isStatistical = false;
	}

	public void setPK(Questionnaire questionnaire, int questionNumber) {
		this.id = new QuestionPK(questionnaire.getId(), questionNumber);
		this.questionnaire = questionnaire;
		this.questionNumber = questionNumber;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isStatistical() {
		return isStatistical;
	}

	public void setStatistical(boolean statistical) {
		isStatistical = statistical;
	}

	public OptionGroup getOptionGroup() {
		return optionGroup;
	}

	public void setOptionGroup(OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
	}

	public InputType getInputType() {
		return inputType;
	}

	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}

	public Set<Answer> getAnswers() {
		return answers;
	}

	public void addAnswer(Answer answer) {
		getAnswers().add(answer);
		answer.setQuestion(this);
		// aligns both sides of the relationship
		// if answer is new, invoking persist() on question cascades also to answer
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Question question = (Question) o;
		return (this.id).equals(question.id);
	}
}
