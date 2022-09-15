package it.polimi.db2.gma.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the MySQLTransaction database table. This entity is
 * only for debugging. It will be used to query the information schema of MySQL
 * and get the MySQL transaction ID, as necessary to illustrate the propagation
 * of transaction across different business components. Note the use of the
 * schema attribute of the Table annotation to map the entity onto a different
 * schema with respect to the other entitites
 */
@Entity
@Table(name = "innodb_trx", schema = "information_schema")
@NamedNativeQuery(name = "MySqlTransaction.getId", query = "SELECT tx.trx_id, tx.trx_state, tx.trx_started FROM information_schema.innodb_trx WHERE tx.trx_mysql_thread_id = connection_id()", resultClass = MySqlTransaction.class)

public class MySqlTransaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "trx_id")
	private int id;

	@Column(name = "trx_state")
	private String trx_state;
	@Temporal(TemporalType.DATE)
	@Column(name = "trx_started")
	private Date trx_started;

	public MySqlTransaction() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTrx_state() {
		return this.trx_state;
	}

	public void setTrx_state(String state) {
		this.trx_state = state;
	}

	public void setTrx_started(Date d) {
		this.trx_started = d;
	}

	public Date getTrx_started() {
		return this.trx_started;
	}


}