package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "questionnaires", schema = "db_gma")
@NamedQueries({
		@NamedQuery(name = "Questionnaire.findQuestionnaireOfTheDay", query = "SELECT q FROM Questionnaire q WHERE q.date = CURRENT_DATE"),
		@NamedQuery(name = "Questionnaire.findQuestionnaireByDay", query = "SELECT q FROM Questionnaire q WHERE q.date = ?1"),
		@NamedQuery(name = "Questionnaire.findAllPastQuestionnaires", query = "SELECT q FROM Questionnaire q WHERE q.date < CURRENT_DATE ORDER BY q.date DESC")
})
public class Questionnaire implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idquestionnaire")
	private int id;

	@Temporal(TemporalType.DATE)
	private Date date;

	@ManyToOne
	@JoinColumn(name = "product", referencedColumnName = "idproduct", nullable = false)
	private Product product;

	@OneToMany(mappedBy = "questionnaire", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
	@OrderBy("questionNumber ASC")
	private List<Question> questions = new ArrayList<>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
	@OrderBy("points DESC")
	private List<Leaderboard> leaderboard = new ArrayList<>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
	@OrderBy("log_timestamp DESC")
	private List<CancelledQuestionnaire> cancelledQuestionnaires = new ArrayList<>();

	public Questionnaire() {
	}

	public Questionnaire(Product product, Date date, List<Question> questions) {
		this.product = product;
		this.date = date;

		int questionNumber = 1;
		for (Question q : questions) {
			q.setPK(this, questionNumber);
			questionNumber++;
		}

		this.questions = questions;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public String getDateString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(this.date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public List<Leaderboard> getLeaderboard() {
		return leaderboard;
	}

	public void setLeaderboard(List<Leaderboard> leaderboard) {
		this.leaderboard = leaderboard;
	}

	public List<CancelledQuestionnaire> getCancelledQuestionnaires() {
		return cancelledQuestionnaires;
	}

	public void setCancelledQuestionnaires(List<CancelledQuestionnaire> cancelledQuestionnaires) {
		this.cancelledQuestionnaires = cancelledQuestionnaires;
	}

	public void addCancelledQuestionnaire(CancelledQuestionnaire cancelledQuestionnaire) {
		cancelledQuestionnaires.add(cancelledQuestionnaire);
	}

	public List<Question> getRequiredQuestions() {
		return getQuestions().stream().filter(q -> !q.isStatistical()).collect(Collectors.toList());
	}

	public List<Question> getStatisticalQuestions() {
		return getQuestions().stream().filter(q -> q.isStatistical()).collect(Collectors.toList());
	}
}