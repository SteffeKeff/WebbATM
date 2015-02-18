package se.dreamteam.atm.service;

import java.util.HashMap;

import se.dreamteam.atm.model.ATMCard;

public abstract class AbstractBank implements Bank
{
	protected final String id;
	protected HashMap<String, ATMCard> cards = new HashMap<>();
	protected HashMap<String, Integer> cardHasMoney = new HashMap<>();
	
	public AbstractBank(String id)
	{
		this.id = id;
	}
}
