/**
IBM AltoroJ
See NOTICE file for license and attribution details.
(c) Copyright IBM Corp. 2008, 2013 All Rights Reserved.
 */
package com.ibm.security.appscan.altoromutual.model;

import java.sql.SQLException;
import java.util.Date;

import com.ibm.security.appscan.altoromutual.util.DBUtil;

/**
 * This class models a user
 * @author Alexei
 *
 */
public class User implements java.io.Serializable{

	private static final long serialVersionUID = -4566649173574593144L;
	
	public static enum Role{User, Admin};
	
	private String username, firstName, lastName;
	private Role role = Role.User;
	
	private Date lastAccessDate = null;
	
	public User(String username, String firstName, String lastName) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		lastAccessDate = new Date();
	}
	
	public void setRole(Role role){
		this.role = role;
	}
	
	public Role getRole(){
		return role;
	}
	
	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public Account[] getAccounts(){
		try {
			return DBUtil.getAccounts(username);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Account lookupAccount(Long accountNumber) {
		for (Account account : getAccounts()) {
			if (account.getAccountId() == accountNumber)
				return account;
		}
		return null;
	}
	
	public long getCreditCardNumber(){
		for (Account account: getAccounts()){
			if (DBUtil.CREDIT_CARD_ACCOUNT_NAME.equals(account.getAccountName()))
				return account.getAccountId();
		}
		return -1L;
	}
	
	public Transaction[] getUserTransactions(String startDate, String endDate, Account[] accounts) throws SQLException {
		
		Transaction[] transactions = null;
		transactions = DBUtil.getTransactions(startDate, endDate, accounts, -1);
		return transactions; 
	}
}
