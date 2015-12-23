package me.flibio.minigamecore.arena;

import me.flibio.minigamecore.events.ArenaStateChangeEvent;

import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Arena {

	private CopyOnWriteArrayList<ArenaState> arenaStates = new CopyOnWriteArrayList<ArenaState>(getDefaultArenaStates());
	private ConcurrentHashMap<ArenaState,Runnable> runnables = new ConcurrentHashMap<ArenaState,Runnable>();
	
	private HashMap<String,Object> customVariables = new HashMap<String,Object>();
	private Location<World> lobbySpawnLocation;
	private Location<World> failedJoinLocation;
	private ConcurrentHashMap<String, Location<World>> spawnLocations = new ConcurrentHashMap<String, Location<World>>();
	private CopyOnWriteArrayList<Player> onlinePlayers = new CopyOnWriteArrayList<Player>();
	private int currentLobbyCountdown;
	private Task lobbyCountdownTask;
	private ArenaState arenaState;
	
	private ArenaOptions arenaOptions;
	
	private Game game;
	private Object plugin;
	
	//TODO - Scoreboard implementation throughout arena
	/**
	 * An arena is an object that can handle spawn locations, lobbies, games, and more.
	 * @param arenaName
	 * 	The name of the arena
	 * @param game
	 * 	An instance of the game
	 * @param plugin
	 * 	An instance of the main class of your plugin
	 */
	public Arena(String arenaName, Game game, Object plugin) {
		this.arenaOptions = new ArenaOptions(arenaName);
		this.game = game;
		this.plugin = plugin;
		this.arenaState = ArenaStates.LOBBY_WAITING;
		
		game.getEventManager().registerListeners(plugin, this);
	}
	
	/**
	 * Adds an online player
	 * @param player
	 * 	The player to add
	 */
	public void addOnlinePlayer(Player player) {
		if(arenaOptions.isDefaultPlayerEventActions()) {
			//Check if the game is in the correct state
			if(arenaState.equals(ArenaStates.LOBBY_WAITING)||arenaState.equals(ArenaStates.LOBBY_COUNTDOWN)) {
				if(onlinePlayers.size()>=arenaOptions.getMaxPlayers()) {
					//Lobby is full
					if(arenaOptions.isDedicatedServer()) {
						//Kick the player
						player.kick(arenaOptions.lobbyFull);
					} else {
						//Try to teleport the player to the failed join location
						if(failedJoinLocation!=null) {
							player.sendMessage(arenaOptions.lobbyFull);
							player.setLocation(failedJoinLocation);
						} else {
							player.kick(arenaOptions.lobbyFull);
						}
					}
				} else {
					//Player can join
					onlinePlayers.add(player);
					for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
						//TODO - replace %name% with the player name
						onlinePlayer.sendMessage(arenaOptions.playerJoined);
					}
					if(lobbySpawnLocation!=null) {
						player.setLocation(lobbySpawnLocation);
					}
					if(arenaState.equals(ArenaStates.LOBBY_WAITING)&&onlinePlayers.size()>=arenaOptions.getMinPlayers()) {
						arenaStateChange(ArenaStates.LOBBY_COUNTDOWN);
					}
				}
			} else {
				if(arenaOptions.isDedicatedServer()) {
					//Kick the player
					player.kick(arenaOptions.gameInProgress);
				} else {
					//Try to teleport the player to the failed join location
					if(failedJoinLocation!=null) {
						player.sendMessage(arenaOptions.gameInProgress);
						player.setLocation(failedJoinLocation);
					} else {
						player.kick(arenaOptions.gameInProgress);
					}
				}
			}
		} else {
			onlinePlayers.add(player);
		}
	}
	
	/**
	 * Removes an online player
	 * @param player
	 * 	The player to remove
	 */
	public void removeOnlinePlayer(Player player) {
		onlinePlayers.remove(player);
		if(arenaOptions.isDefaultPlayerEventActions()) {
			if(arenaState.equals(ArenaStates.LOBBY_COUNTDOWN)&&onlinePlayers.size()<arenaOptions.getMinPlayers()) {
				arenaStateChange(ArenaStates.COUNTDOWN_CANCELLED);
			}
			for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
				//TODO - replace %name% with the player name
				onlinePlayer.sendMessage(arenaOptions.playerQuit);
			}
		}
	}
	
	/**
	 * Gets all of the players in an arena
	 * @return
	 * 	All the players in the arena
	 */
	public CopyOnWriteArrayList<Player> getOnlinePlayers() {
		return onlinePlayers;
	}
	
	/**
	 * Calls an state change on the arena
	 * @param changeTo
	 * 	The state to change the arena to
	 */
	public void arenaStateChange(ArenaState changeTo) {
		if(!arenaStates.contains(changeTo)) {
			return;
		}
		arenaState = changeTo;
		//Post the arena state change event
		game.getEventManager().post(new ArenaStateChangeEvent(this));
		//Run a runnable if it is set
		if(arenaStateRunnableExists(changeTo)) {
			runnables.get(changeTo).run();
		}
		//Run default actions ifthey are enabled
		if(arenaOptions.isDefaultStateChangeActions()) {
			if(arenaState.equals(ArenaStates.LOBBY_COUNTDOWN)) {
				currentLobbyCountdown = arenaOptions.getLobbyCountdownTime();
				for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
					//TODO - replace %time% with the seconds to go
					onlinePlayer.sendMessage(arenaOptions.lobbyCountdownStarted);
				}
				//Register task the run the countdown every 1 second
				lobbyCountdownTask = game.getScheduler().createTaskBuilder().execute(new Runnable() {
					@Override
					public void run() {
						if(currentLobbyCountdown==0) {
							arenaStateChange(ArenaStates.GAME_COUNTDOWN);
							lobbyCountdownTask.cancel();
							return;
						}
						if(arenaOptions.getLobbyCountdownTime()/2==currentLobbyCountdown||
								currentLobbyCountdown<=10) {
							//Send a message
							for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
								//TODO - replace %time% with the seconds to go
								onlinePlayer.sendMessage(arenaOptions.lobbyCountdownProgress);
							}
						}
						currentLobbyCountdown--;
					}
				}).async().interval(1,TimeUnit.SECONDS).submit(plugin);
			} else if(arenaState.equals(ArenaStates.COUNTDOWN_CANCELLED)) {
				currentLobbyCountdown = arenaOptions.getLobbyCountdownTime();
				if(lobbyCountdownTask!=null) {
					lobbyCountdownTask.cancel();
				}
				arenaState = ArenaStates.LOBBY_WAITING;
				for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
					onlinePlayer.sendMessage(arenaOptions.lobbyCountdownCancelled);
				}
			} else if(arenaState.equals(ArenaStates.GAME_COUNTDOWN)) {
				currentLobbyCountdown = arenaOptions.getLobbyCountdownTime();
				//TODO
				arenaStateChange(ArenaStates.GAME_PLAYING);
			} else if(arenaState.equals(ArenaStates.GAME_OVER)) {
				for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
					onlinePlayer.sendMessage(arenaOptions.gameOver);
					//End game spectator only works with end game delay on
					if(arenaOptions.isEndGameDelay()&&arenaOptions.isEndGameSpectator()) {
						//TODO - Save the gamemode
						onlinePlayer.offer(Keys.GAME_MODE,GameModes.SPECTATOR);
					}
				}
				if(arenaOptions.isEndGameDelay()) {
					game.getScheduler().createTaskBuilder().execute(new Runnable() {
						@Override
						public void run() {
							for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
								onlinePlayer.sendMessage(arenaOptions.gameOver);
								if(arenaOptions.isEndGameSpectator()) {
									//TODO load the gamemode
									onlinePlayer.offer(Keys.GAME_MODE,GameModes.SURVIVAL);
								}
								if(arenaOptions.isDedicatedServer()) {
									onlinePlayer.kick(arenaOptions.gameOver);
								} else {
									if(lobbySpawnLocation!=null) {
										onlinePlayer.setLocation(lobbySpawnLocation);
									}
								}
							}
						}
					}).async().delay(5,TimeUnit.SECONDS).submit(plugin);
				} else {
					//No delay
					for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
						onlinePlayer.sendMessage(arenaOptions.gameOver);
						if(arenaOptions.isEndGameSpectator()) {
							//TODO load the gamemode
							onlinePlayer.offer(Keys.GAME_MODE,GameModes.SURVIVAL);
						}
						if(arenaOptions.isDedicatedServer()) {
							onlinePlayer.kick(arenaOptions.gameOver);
						} else {
							if(lobbySpawnLocation!=null) {
								onlinePlayer.setLocation(lobbySpawnLocation);
							}
						}
					}
				}
			}
		}
	}
	
	//Spawn Locations
	
	/**
	 * Gets all of the possible spawn locations
	 * @return
	 * 	All of the possible spawn locations
	 */
	public ConcurrentHashMap<String, Location<World>> getSpawnLocations() {
		return spawnLocations;
	}
	
	/**
	 * Adds a spawn location to the list of possible spawn locations
	 * @param name
	 * 	The name of the spawn location
	 * @param location
	 * 	The location to add
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean addSpawnLocation(String name, Location<World> location) {
		if(spawnLocations.containsKey(name)) {
			return false;
		}
		spawnLocations.put(name, location);
		return true;
	}
	
	/**
	 * Removes a spawn location from the list of possible spawn locations
	 * @param name
	 * 	The name of the spawn location
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean removeSpawnLocation(String name) {
		if(!spawnLocations.containsKey(name)) {
			return false;
		}
		spawnLocations.remove(name);
		return true;
	}
	
	/**
	 * Gets a spawn location by name
	 * @param name
	 * 	The name of the spawn location to get
	 * @return
	 * 	Optional of the spawn location
	 */
	public Optional<Location<World>> getSpawnLocation(String name) {
		if(!spawnLocations.containsKey(name)) {
			return Optional.empty();
		}
		return Optional.of(spawnLocations.get(name));
	}
	
	/**
	 * Selects a random spawn location from the list of available spawn locations
	 * @return
	 * 	Optional of the spawn location
	 */
	public Optional<Location<World>> randomSpawnLocation() {
		if(spawnLocations.isEmpty()) {
			return Optional.empty();
		}
		//TODO
		return Optional.empty();
	}
	
	/**
	 * Disperses the players among all the spawn locations
	 */
	public void dispersePlayers() {
		//TODO
	}
	
	//Other Arena Properties
	
	/**
	 * Sets the location a player will teleport to if 
	 * they failed to join the arena(Non-Dedicated Arenas Only)
	 * @param location
	 * 	The location to set the failed join location to
	 */
	public void setFailedJoinLocation(Location<World> location) {
		failedJoinLocation = location;
	}
	
	/**
	 * Gets the set of arena options
	 * @return
	 * 	The ArenaOptions
	 */
	public ArenaOptions getOptions() {
		return arenaOptions;
	}
	
	/**
	 * Gets the state of the arena
	 * @return
	 * 	The state of the arena
	 */
	public ArenaState getArenaState() {
		return arenaState;
	}
	
	/**
	 * Adds a new arena state
	 * @param state
	 * 	The arena state to add
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean addArenaState(ArenaState state) {
		//Check ifthe state exists
		if(arenaStateExists(state)) {
			return false;
		} else {
			arenaStates.add(state);
			return true;
		}
	}
	
	/**
	 * Removes an arena state
	 * @param state
	 * 	The arena state to remove
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean removeArenaState(ArenaState state) {
		//Check ifthe state is a default state
		if(getDefaultArenaStates().contains(state)||!arenaStateExists(state)) {
			return false;
		} else {
			if(runnables.keySet().contains(state)) {
				runnables.remove(state);
			}
			arenaStates.remove(state);
			return true;
		}
	}
	
	/**
	 * Checks if an arena state exists
	 * @param arenaState
	 * 	The arena state to check for
	 * @return
	 * 	If the arena state exists
	 */
	public boolean arenaStateExists(ArenaState arenaState) {
		return arenaStates.contains(arenaState);
	}
	
	/**
	 * Gets a list of the default arena states
	 * @return
	 * 	A list of the default arena states
	 */
	public List<ArenaState> getDefaultArenaStates() {
		return Arrays.asList(ArenaStates.LOBBY_WAITING,ArenaStates.LOBBY_COUNTDOWN,ArenaStates.GAME_COUNTDOWN,
				ArenaStates.GAME_PLAYING,ArenaStates.GAME_OVER,ArenaStates.COUNTDOWN_CANCELLED);
	}
	
	/**
	 * Adds an arena state runnable
	 * @param state
	 * 	The state to add
	 * @param runnable
	 * 	The runnable to add
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean addArenaStateRunnable(ArenaState state, Runnable runnable) {
		if(!arenaStateExists(state)||arenaStateRunnableExists(state)) {
			return false;
		}
		runnables.put(state, runnable);
		return true;
	}
	
	/**
	 * Removes an arena state runnable
	 * @param state
	 * 	The arena state to remove
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean removeArenaStateRunnable(ArenaState state) {
		if(!arenaStateExists(state)||!arenaStateRunnableExists(state)) {
			return false;
		}
		runnables.remove(state);
		return true;
	}
	
	/**
	 * Checks if an arena state runnable exists
	 * @param state
	 * 	The state to check for
	 * @return
	 * 	If the arena state runnable exists
	 */
	public boolean arenaStateRunnableExists(ArenaState state) {
		return runnables.keySet().contains(state);
	}
	
	/**
	 * Gets an arena state runnable
	 * @param state
	 * 	The state to get the runnable of
	 * @return
	 * 	The arena state runnable
	 */
	public Optional<Runnable> getArenaStateRunnable(ArenaState state) {
		if(arenaStateRunnableExists(state)) {
			return Optional.of(runnables.get(state));
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Sets the lobby spawn location of the arena
	 * @param location
	 * 	The lobby spawn location of the arena
	 */
	public void setLobbySpawnLocation(Location<World> location) {
		lobbySpawnLocation = location;
	}
	
	/**
	 * Gets the lobby spawn location of the arena
	 * @return
	 * 	The lobby spawn location
	 */
	public Optional<Location<World>> getLobbySpawnLocation() {
		if(lobbySpawnLocation==null) {
			return Optional.empty();
		} else {
			return Optional.of(lobbySpawnLocation);
		}
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
	 * Returns all of the custom variables stored with the arena
	 * @return
	 * 	A HashMap of all of the custom variables
	 */
	public HashMap<String,Object> getCustomVariables() {
		final HashMap<String,Object> toReturn = this.customVariables;
		return toReturn;
	}

	//Listeners
	
	@Listener
	public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
		if(arenaOptions.isDedicatedServer()&&arenaOptions.isTriggerPlayerEvents()) {
			Player player = event.getTargetEntity();
			removeOnlinePlayer(player);
			event.setSink(MessageSinks.toNone());
		}
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		if(arenaOptions.isDedicatedServer()&&arenaOptions.isTriggerPlayerEvents()) {
			Player player = event.getTargetEntity();
			addOnlinePlayer(player);
			event.setSink(MessageSinks.toNone());
		}
	}
}
