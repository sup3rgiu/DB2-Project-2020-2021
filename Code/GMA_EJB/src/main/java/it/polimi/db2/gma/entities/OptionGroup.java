package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;

@Entity
@Table(name = "option_groups", schema = "db_gma")
public class OptionGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idoption")
	private int id;

	private String name;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "group", cascade = CascadeType.ALL)
	private List<OptionChoice> choices;

	public OptionGroup() {
	}

	public OptionGroup(List<OptionChoice> choices) {
		for (OptionChoice c : choices) {
			c.setGroup(this);
		}
		this.choices = choices;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<OptionChoice> getChoices() {
		return choices;
	}

	public Integer getMinChoice() {
		Integer min = null;
		try {
			min = choices.stream().mapToInt(c -> Integer.parseInt(c.getChoice())).min().orElseThrow();
		} catch (NoSuchElementException | NumberFormatException e) {
			return null;
		}
		return min;
	}

	public Integer getMaxChoice() {
		Integer max = null;
		try {
			max = choices.stream().mapToInt(c -> Integer.parseInt(c.getChoice())).max().orElseThrow();
		} catch (NoSuchElementException | NumberFormatException e) {
			return null;
		}
		return max;
	}

}