package me.flibio.minigamecore.teams;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Team {
	
	private String name;
	private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
	
	public Team(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the team name
	 * @return
	 * 	The name of the team
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets a player from the team players
	 * @param name
	 * 	The name of the player to search for
	 * @return
	 * 	The player
	 */
	public Optional<Player> getPlayer(String name) {
		for(Player player : players) {
			if(player.getName().equalsIgnoreCase(name)) {
				return Optional.of(player);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if a player is on the team
	 * @param name
	 * 	The name of the player to check for
	 * @return
	 * 	If the player is on the team or not
	 */
	public boolean playerExists(String name) {
		for(Player player : players) {
			if(player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a player to the team
	 * @param player
	 * 	The player to add to the team
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	/**
	 * Removes a player from the team
	 * @param name
	 * 	The name of the player to remove from the team
	 */
	public void removePlayer(String name) {
		for(Player player : players) {
			if(player.getName().equalsIgnoreCase(name)) {
				players.remove(player);
			}
		}
	}
}
