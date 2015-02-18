package se.dreamteam.atm.service;

import se.dreamteam.atm.exception.ATMException;
import se.dreamteam.atm.model.ATMCard;
import se.dreamteam.atm.model.BankReceipt;

public final class BankImpl extends AbstractBank
{
	
	public BankImpl(String id)
	{
		super(id);
	}
	
	public void addCard(ATMCard card)
	{
		if(!hasCard(card.getAccountHolderId()))
		{
			cards.put(card.getAccountHolderId(), card);
			cardHasMoney.put(card.getAccountHolderId(), 0);
			return;
		}
		
		throw new ATMException("card already exist!");
	}
	
	public ATMCard getCard(String accountHolderId)
	{
		if(cards.containsKey(accountHolderId)){
			return cards.get(accountHolderId);
		}
		throw new ATMException("User does not exist");
	}
	
	public void addMoney(String accountHolderId, int money)
	{
		int currentMoney = this.cardHasMoney.get(accountHolderId);
		cardHasMoney.replace(accountHolderId, currentMoney+money);
	}
	
	public boolean hasCard(String name)
	{
		return cards.containsKey(name);
	}
	
	@Override
	public String getBankId()
	{
		return id;
	}

	@Override
	public long getBalance(String accountHolderId)
	{
		return cardHasMoney.get(accountHolderId);
	}

	@Override
	public long withdrawAmount(String accountHolderId, int amount)
	{
		int balance = cardHasMoney.get(accountHolderId)-amount;
		cardHasMoney.replace(accountHolderId, balance);
		return balance;
	}

	@Override
	public BankReceipt requestReceipt(long transactionId, int amount)
	{
		return new BankReceipt(id, transactionId, amount);
	}

}
