package it.polimi.db2.gma.services;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionSynchronizationRegistry;

public class JPATxUtils {

	public JPATxUtils() {
	}

	public static String txStatusConvert(int code) {

		String status = null;
		switch (code) {
			case 0:
				status = "STATUS_ACTIVE";
				break;
			case 1:
				status = "STATUS_MARKED_ROLLBACK";
				break;
			case 2:
				status = "STATUS_PREPARED";
				break;
			case 3:
				status = "STATUS_COMMITTED";
				break;
			case 4:
				status = "STATUS_ROLLEDBACK";
			case 5:
				status = "STATUS_ACTIVE";
				break;
			case 6:
				status = "STATUS_NO_TRANSACTION";
				break;
			case 7:
				status = "STATUS_PREPARING";
				break;
			case 8:
				status = "STATUS_COMMITTING";
			case 9:
				status = "STATUS_ROLLING_BACK";
				break;
		}
		return status;
	}

	public static void printTxId() {
		TransactionSynchronizationRegistry txReg = null;
		try {
			txReg = (TransactionSynchronizationRegistry) new InitialContext()
					.lookup("java:comp/TransactionSynchronizationRegistry");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		System.out.println("JTA TX Id: " + txReg.getTransactionKey());

	}


	public static void printTxStatus() {
		TransactionSynchronizationRegistry txReg = null;
		try {
			txReg = (TransactionSynchronizationRegistry) new InitialContext()
					.lookup("java:comp/TransactionSynchronizationRegistry");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		System.out.println("JTA TX status: " + txStatusConvert(txReg.getTransactionStatus()));
	}

}
