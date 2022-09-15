package it.polimi.db2.gma.entities.primaryKeys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReviewPK implements Serializable {

	@Column(name = "user_id")
	private int userId;

	@Column(name = "product_id")
	private int productId;

	public ReviewPK() {
	}

	public int getUserId() {
		return userId;
	}

	public int getProductId() {
		return productId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReviewPK reviewPK = (ReviewPK) o;
		return userId == reviewPK.userId && productId == reviewPK.productId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, productId);
	}
}