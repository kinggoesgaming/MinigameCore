package me.Flibio.MinigameCore.Economy;

import java.util.UUID;

import org.spongepowered.api.Game;

public class EconomyManager {
	
	public EconomyManager(Game game) {
		
	}
	
	/**
	 * Sets the balance of a player
	 * @param uuid
	 * 	UUID of the player whose balance to change
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean setBalance(UUID uuid, double amount) {
		//TODO
		return false;
	}
	
	/**
	 * Gets the balance of a player
	 * @param uuid
	 * 	UUID of the player to get the balance of
	 * @return
	 * 	The balance of the player
	 */
	public double getBalance(UUID uuid) {
		//TODO
		return -1;
	}
	
	/**
	 * Adds currency to a players balance
	 * @param uuid
	 * 	UUID of the player whose balance to change
	 * @param amount
	 * 	Amount of currency to add to the player
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean addCurrency(UUID uuid, double amount) {
		//TODO
		return false;
	}
	
	/**
	 * Removes currency from a players balance
	 * @param uuid
	 * 	UUID of the player whose balance to change
	 * @param amount
	 * 	Amount of currency to remove from the player
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean removeCurrency(UUID uuid, double amount) {
		//TODO
		return false;
	}
	
}
