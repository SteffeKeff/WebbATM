package se.dreamteam.atm.model;

import java.util.Date;

public final class BankReceipt
{
	private final String bankId;
	private final long transactionId;
	private final int amount;
	private final Date date;

	public BankReceipt(final String bankId, final long transactionId, final int amount)
	{
		this.bankId = bankId;
		this.transactionId = transactionId;
		this.amount = amount;
		this.date = new Date();
	}

	public String getBankId()
	{
		return bankId;
	}

	public long getTransactionId()
	{
		return transactionId;
	}

	public int getAmount()
	{
		return amount;
	}

	public Date getDate()
	{
		return date;
	}
}