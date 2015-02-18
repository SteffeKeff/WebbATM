package se.dreamteam.atm.service;

import java.util.HashMap;
import java.util.Map;

import se.dreamteam.atm.exception.ATMException;
import se.dreamteam.atm.exception.ATMSecurityException;
import se.dreamteam.atm.model.ATMCard;

public final class ATM
{
	private Map<String, BankImpl> banks = new HashMap<>();

	public ATMSession verifyPin(final int pin, final ATMCard card)
	{
		if (getBank(card.getBankId()).getCard(card.getAccountHolderId()).verifyPin(pin))
		{
			return new ATMSessionImpl(card, getBank(card.getBankId()));
		}
		throw new ATMSecurityException("Invalid pincode");
	}
	
	public void addCard(ATMCard atmCard)
	{
		getBank(atmCard.getBankId()).addCard(atmCard);
	}
	
	public boolean hasCard(final ATMCard atmCard)
	{
		return getBank(atmCard.getBankId()).hasCard(atmCard.getAccountHolderId());
	}
	
	public void addBank(final BankImpl bank)
	{
		if(!banks.containsKey(bank.getBankId()))
		{
			banks.put(bank.getBankId(), bank);
			return;
		}
		
		throw new ATMException("Bank already exist!");
	}
	
	public boolean hasBank(String bankId)
	{
		return banks.containsKey(bankId);
	}
	
	private BankImpl getBank(final String bankId)
	{
		if (hasBank(bankId))
		{
			return banks.get(bankId);
		}
		throw new ATMException("Bank does not exist");
	}
}