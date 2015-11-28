package me.flibio.minigamecore.economy;

import me.Flibio.EconomyLite.API.EconomyLiteAPI;

import org.spongepowered.api.Game;

import java.util.Optional;
import java.util.UUID;

public class EconomyManager {
	
	private boolean firstRun = true;
	private boolean economyInstalled = false;
	
	private EconomyLiteAPI economyAPI;
	
	private Game game;
	
	//TODO - Support for TotalEconomy
	/**
	 * Provides easy-to-use economy integration
	 * @param game
	 * 	The game object
	 */
	public EconomyManager(Game game) {
		this.game = game;
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
	public boolean setBalance(UUID uuid, double amount) {
		if(firstRun) {
			checkForEconomy();
		}
		if(!economyInstalled) {
			return false;
		}
		if(amount<0) {
			return false;
		}
		return economyAPI.getPlayerAPI().setBalance(uuid.toString(), Integer.valueOf((int) Math.round(amount)));
	}
	
	/**
	 * Gets the balance of a player
	 * @param uuid
	 * 	UUID of the player to get the balance of
	 * @return
	 * 	The balance of the player
	 */
	public Optional<Double> getBalance(UUID uuid) {
		if(firstRun) {
			checkForEconomy();
		}
		if(!economyInstalled) {
			Optional.empty();
		}
		int bal = economyAPI.getPlayerAPI().getBalance(uuid.toString());
		if(bal<0) {
			return Optional.empty();
		}
		return Optional.of((double) bal);
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
	public boolean addCurrency(UUID uuid, double amount) {
		if(firstRun) {
			checkForEconomy();
		}
		if(!economyInstalled) {
			return false;
		}
		if(amount<0) {
			return false;
		}
		Optional<Double> currentBalance = getBalance(uuid);
		if(!currentBalance.isPresent()) {
			return false;
		}
		return setBalance(uuid, amount + currentBalance.get());
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
	public boolean removeCurrency(UUID uuid, double amount) {
		if(firstRun) {
			checkForEconomy();
		}
		if(!economyInstalled) {
			return false;
		}
		if(amount<0) {
			return false;
		}
		Optional<Double> currentBalance = getBalance(uuid);
		if(!currentBalance.isPresent()) {
			return false;
		}
		return setBalance(uuid, currentBalance.get() - amount);
	}
	
	private void checkForEconomy() {
		firstRun = false;
		if(game.getPluginManager().getPlugin("EconomyLite").isPresent()) {
			Optional<EconomyLiteAPI> service = game.getServiceManager().provide(EconomyLiteAPI.class);
			if(service.isPresent()) {
				economyInstalled = true;
				economyAPI = service.get();
			}
		}
	}
	
}
