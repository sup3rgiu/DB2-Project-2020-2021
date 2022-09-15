package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "input_types", schema = "db_gma")
@NamedQuery(name = "InputType.getInputTypeByValue", query = "SELECT i FROM InputType i WHERE i.type = ?1")
public class InputType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idinput")
	private int id;

	@Enumerated(EnumType.STRING)
	private InputTypeValue type;

	public InputType() {
	}

	public InputType(InputTypeValue type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public InputTypeValue getType() {
		return type;
	}

	public void setType(InputTypeValue type) {
		this.type = type;
	}
}