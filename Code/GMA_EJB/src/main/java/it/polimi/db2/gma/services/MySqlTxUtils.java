package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.MySqlTransaction;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class MySqlTxUtils {
	@PersistenceContext(unitName = "GMA_EJB")
	EntityManager em;

	public MySqlTxUtils() {
	}

	public void printMySQLTxStatus() {
		List<MySqlTransaction> MySqlTxList = null;
		try {
			/*
			 * A native query in SQL on the proprietary schema where MySql stores
			 * transaction info WARNING: this is not portable to other DBMSs
			 */
			Query q = em.createNativeQuery("SELECT tx.trx_id, tx.trx_state, trx_started FROM information_schema.innodb_trx tx WHERE tx.trx_mysql_thread_id = connection_id()",
					MySqlTransaction.class);
			MySqlTxList = (List<MySqlTransaction>) q.getResultList();
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		for (MySqlTransaction t : MySqlTxList) {
			System.out.println("MySQl native TX_id ....." + t.getId());
			System.out.println("MySQl native TX_state ....." + t.getTrx_state());
			System.out.println("MySQl native TX_started ....." + t.getTrx_started());
		}
	}
}
