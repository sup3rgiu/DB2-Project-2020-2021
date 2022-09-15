package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.primaryKeys.ReviewPK;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reviews", schema = "db_gma")
public class Review implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ReviewPK id;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id", referencedColumnName = "iduser", nullable = false)
	private User user;

	@ManyToOne
	@MapsId("productId")
	@JoinColumn(name = "product_id", referencedColumnName = "idproduct", nullable = false)
	private Product product;

	private String comment;

	public Review() {
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}