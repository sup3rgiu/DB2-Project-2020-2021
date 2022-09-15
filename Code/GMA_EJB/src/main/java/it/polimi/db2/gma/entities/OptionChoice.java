package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "option_choices", schema = "db_gma")
public class OptionChoice implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idchoice")
	private int id;

	private String choice;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private OptionGroup group;

	public OptionChoice() {
	}

	public OptionChoice(String choice) {
		this.choice = choice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public OptionGroup getGroup() {
		return group;
	}

	public void setGroup(OptionGroup group) {
		this.group = group;
	}
}