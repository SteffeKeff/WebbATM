package se.dreamteam.atm.service;

import se.dreamteam.atm.model.ATMCard;

public abstract class AbstractATMSession implements ATMSession
{
	protected final ATMCard atmCard;
	protected final BankImpl bank;
	
	public AbstractATMSession(final ATMCard atmCard, final BankImpl bank)
	{
		this.atmCard = atmCard;
		this.bank = bank;
	}
}