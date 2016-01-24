package me.flibio.minigamecore.economy;

import me.flibio.minigamecore.main.MinigameCore;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class EconomyManager {
	
	private boolean foundProvider = true;
	
	private EconomyService economy;
	
	private Cause cause = Cause.of("MinigameCore");
	private Currency currency;
	
	/**
	 * Provides easy-to-use economy integration
	 * @param game
	 * 	The game object
	 */
	public EconomyManager(Game game) {
		game.getEventManager().registerListeners(MinigameCore.access, this);
	}
	
	@Listener
	public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
		if(event.getService().equals(EconomyService.class)&&!foundProvider) {
			Object raw = event.getNewProviderRegistration().getProvider();
			if(raw instanceof EconomyService) {
				foundProvider = true;
				economy = (EconomyService) raw;
				currency = economy.getDefaultCurrency();
			} else {
				foundProvider = false;
			}
		}
	}
	
	/**
	 * Sets the balance of a player
	 * @param uuid
	 * 	UUID of the player whose balance to change
	 * @param amount
	 * 	The amount to set the player's balance to
	 * @return
	 * 	Boolean based on if  the method was successful or not
	 */
	public boolean setBalance(UUID uuid, BigDecimal amount) {
		if(!foundProvider) return false;
		Optional<UniqueAccount> uOpt = economy.getAccount(uuid);
		if(!uOpt.isPresent()) return false;
		UniqueAccount account = uOpt.get();
		if(account.setBalance(currency, amount, cause).getResult().equals(ResultType.SUCCESS)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the balance of a player
	 * @param uuid
	 * 	UUID of the player to get the balance of
	 * @return
	 * 	The balance of the player
	 */
	public Optional<BigDecimal> getBalance(UUID uuid) {
		if(!foundProvider) return Optional.empty();
		Optional<UniqueAccount> uOpt = economy.getAccount(uuid);
		if(!uOpt.isPresent()) return Optional.empty();
		UniqueAccount account = uOpt.get();
		return Optional.of(account.getBalance(currency));
	}
	
	/**
	 * Adds currency to a players balance
	 * @param uuid
	 * 	UUID of the player whose balance to change
	 * @param amount
	 * 	Amount of currency to add to the player
	 * @return
	 * 	Boolean based on if  the method was successful or not
	 */
	public boolean addCurrency(UUID uuid, BigDecimal amount) {
		if(!foundProvider) return false;
		Optional<UniqueAccount> uOpt = economy.getAccount(uuid);
		if(!uOpt.isPresent()) return false;
		UniqueAccount account = uOpt.get();
		if(account.deposit(currency, amount, cause).getResult().equals(ResultType.SUCCESS)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes currency from a players balance
	 * @param uuid
	 * 	UUID of the player whose balance to change
	 * @param amount
	 * 	Amount of currency to remove from the player
	 * @return
	 * 	Boolean based on if  the method was successful or not
	 */
	public boolean removeCurrency(UUID uuid, BigDecimal amount) {
		if(!foundProvider) return false;
		Optional<UniqueAccount> uOpt = economy.getAccount(uuid);
		if(!uOpt.isPresent()) return false;
		UniqueAccount account = uOpt.get();
		if(account.withdraw(currency, amount, cause).getResult().equals(ResultType.SUCCESS)) {
			return true;
		} else {
			return false;
		}
	}
	
}
