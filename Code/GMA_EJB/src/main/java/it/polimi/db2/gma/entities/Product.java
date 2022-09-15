package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "products", schema = "db_gma")
@NamedQuery(name = "Product.getAllProducts", query = "SELECT p FROM Product p")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idproduct")
	private int id;

	private String name;

	@Basic(fetch = FetchType.LAZY)
	@Lob
	private byte[] image;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
	private List<Questionnaire> questionnaires = new ArrayList<>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "product", cascade = CascadeType.ALL)
	private Set<Review> reviews = new HashSet<>();

	public Product() {
	}

	public int getId() {
		return this.id;
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

	public byte[] getImage() {
		return image;
	}

	public String getImageData() {
		return Base64.getMimeEncoder().encodeToString(image);
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public List<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(List<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}

	public Set<Review> getReviews() {
		return reviews;
	}
}