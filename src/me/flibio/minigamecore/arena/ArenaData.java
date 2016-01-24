package me.flibio.minigamecore.arena;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;

public class ArenaData {
	
	private HashMap<String,Object> customVariables = new HashMap<String,Object>();
	private HashMap<String,Location<World>> customLocations = new HashMap<String,Location<World>>();

	private String name = "";
	private boolean triggerPlayerEvents = true;
	private boolean modifyLobbyBlocks = false;
	private boolean allowLobbyDamage = false;
	
	/**
	 * Stores configurable options for the arena
	 * @param arenaName
	 * 	The name of the arena
	 */
	public ArenaData(String arenaName) {
		this.name = arenaName;
	}

	/**
	 * Gets the name of the arena
	 * @return
	 * 	The name of the arena
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if MinigameCore will automatically trigger player join 
	 * and player quit methods
	 * @return
	 * 	If MinigameCore will automatically trigger player join 
	 * 	and player quit methods
	 */
	public boolean isTriggerPlayerEvents() {
		return triggerPlayerEvents;
	}

	/**
	 * Sets if MinigameCore will automatically trigger player join 
	 * and player quit methods
	 * @param triggerPlayerEvents
	 * 	If MinigameCore will automatically trigger player join 
	 * 	and player quit methods
	 */
	public void setTriggerPlayerEvents(boolean triggerPlayerEvents) {
		this.triggerPlayerEvents = triggerPlayerEvents;
	}

	/**
	 * Checks if MinigameCore will block players from modifying blocks in the 
	 * lobby.
	 * @return
	 * 	If MinigameCore will block players from modifying blocks in the lobby
	 */
	public boolean isModifyLobbyBlocks() {
		return modifyLobbyBlocks;
	}

	/**
	 * Sets if MinigameCore will block players from modifying blocks in the 
	 * lobby.
	 * @param defaultPlayerEventActions
	 * 	If MinigameCore will block players from modifying blocks in the lobby
	 */
	public void setModifyLobbyBlocks(boolean modifyLobbyBlocks) {
		this.modifyLobbyBlocks = modifyLobbyBlocks;
	}

	/**
	 * Checks if MinigameCore will allow players to take damage in the lobby
	 * @return
	 * 	If MinigameCore will allow players to take damage in the lobby
	 */
	public boolean isAllowLobbyDamage() {
		return allowLobbyDamage;
	}

	/**
	 * Sets if MinigameCore will allow players to take damage in the lobby
	 * @param allowLobbyDamage
	 * 	If MinigameCore will allow players to take damage in the lobby
	 */
	public void setAllowLobbyDamage(boolean allowLobbyDamage) {
		this.allowLobbyDamage = allowLobbyDamage;
	}
	
	//Custom Variables
	
	/**
	 * Sets a custom variable. Saved with the arena data.
	 * @param key
	 * 	The key used to retrieve the value
	 * @param value
	 * 	The value to be saved
	 */
	public void setVariable(String key, String value) {
		customVariables.put(key, value);
	}
	
	/**
	 * Sets a custom variable. Saved with the arena data.
	 * @param key
	 * 	The key used to retrieve the value
	 * @param value
	 * 	The value to be saved
	 */
	public void setVariable(String key, int value) {
		customVariables.put(key, value);
	}
	
	/**
	 * Sets a custom variable. Saved with the arena data.
	 * @param key
	 * 	The key used to retrieve the value
	 * @param value
	 * 	The value to be saved
	 */
	public void setVariable(String key, boolean value) {
		customVariables.put(key, value);
	}
	
	/**
	 * Sets a custom variable. Saved with the arena data.
	 * @param key
	 * 	The key used to retrieve the value
	 * @param value
	 * 	The value to be saved
	 */
	public void setVariable(String key, double value) {
		customVariables.put(key, value);
	}
	
	/**
	 * Sets a custom variable. Saved with the arena data.
	 * @param key
	 * 	The key used to retrieve the value
	 * @param value
	 * 	The value to be saved
	 */
	public void setVariable(String key, float value) {
		customVariables.put(key, value);
	}
	
	/**
	 * Retrieves a value using the specified key
	 * @param key
	 * 	The key to use to retrieve the value
	 * @return
	 * 	The value, if found
	 */
	public Optional<Object> getVariable(String key) {
		if(customVariables.containsKey(key)) {
			return Optional.of(customVariables.get(key));
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Gets all of the custom variables stored with the arena
	 * @return
	 * 	A HashMap of all of the custom variables
	 */
	public HashMap<String,Object> getCustomVariables() {
		final HashMap<String,Object> toReturn = this.customVariables;
		return toReturn;
	}
	
	/**
	 * Sets a custom location. Saved with the arena data.
	 * @param key
	 * 	The key used to retrieve the location
	 * @param location
	 * 	The location to be saved
	 */
	public void setLocation(String key, Location<World> location) {
		customLocations.put(key, location);
	}
	
	/**
	 * Gets a location using the specified key
	 * @param key
	 * 	The key of the location to get
	 * @return
	 * 	The location, if found
	 */
	public Optional<Location<World>> getLocation(String key) {
		if(customLocations.containsKey(key)) {
			return Optional.of(customLocations.get(key));
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Gets all of the custom locations stored with the arena
	 * @return
	 * 	A HashMap of all of the custom locations
	 */
	public HashMap<String,Location<World>> getCustomLocations() {
		final HashMap<String,Location<World>> toReturn = this.customLocations;
		return toReturn;
	}
	
}
