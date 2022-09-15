package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "offensive_words", schema = "db_gma")
@NamedQuery(name = "OffensiveWord.getOffensiveWords", query = "SELECT o FROM OffensiveWord o")
@NamedNativeQuery(name = "OffensiveWord.getBannedWordsInString", query = "SELECT * FROM db_gma.offensive_words WHERE MATCH(word) AGAINST(?1 IN NATURAL LANGUAGE MODE)", resultClass = OffensiveWord.class)
public class OffensiveWord implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	private String word;

	public OffensiveWord() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}