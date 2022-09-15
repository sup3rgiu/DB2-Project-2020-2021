package it.polimi.db2.gma.entities;

import javax.persistence.*;

@Entity
@Table(name = "fixed_questions", schema = "db_gma")
@NamedQuery(name = "FixedQuestion.getAllFixedQuestions", query = "SELECT q FROM FixedQuestion q")
public class FixedQuestion {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idquestion")
	private int id;

	private String text;

	@ManyToOne
	@JoinColumn(name = "option_group_id", referencedColumnName = "idoption")
	private OptionGroup optionGroup;

	@ManyToOne
	@JoinColumn(name = "input_type_id", referencedColumnName = "idinput", nullable = false)
	private InputType inputType;

	public FixedQuestion() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
}
