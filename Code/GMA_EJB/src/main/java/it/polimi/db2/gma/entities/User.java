package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", schema = "db_gma")
@NamedQueries({
		@NamedQuery(name = "User.checkCredentials", query = "SELECT r FROM User r WHERE r.username = ?1 and r.password = ?2"),
		@NamedQuery(name = "User.findUserByUsername", query = "SELECT u FROM User u WHERE u.username = ?1"),
		@NamedQuery(name = "User.findUserByEmail", query = "SELECT u FROM User u WHERE u.email = ?1")
})

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "iduser")
	private int id;

	private String username;
	private String password;
	private String email;
	private boolean blocked;
	private boolean admin;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Review> reviews = new HashSet<>();

	public User() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

}