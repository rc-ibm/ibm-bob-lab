/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
 */

package com.ibm.security.appscan.altoromutual.model;

import java.util.Date;

/**
 * This class models a transactions
 * @author Alexei
 *
 */
public class Transaction {

	private int transactionId;
	private long accountId;
	private String transactionType;
	private double amount;
	private Date date;
	
	public Transaction(int transactionId, long accountId, Date date, String transactionType, double amount) {
		this.accountId = accountId;
		this.amount = amount;
		this.transactionId = transactionId;
		this.transactionType = transactionType;
		this.date = date;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public long getAccountId() {
		return accountId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public double getAmount() {
		return amount;
	}

	public Date getDate() {
		return date;
	}
}
