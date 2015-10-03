package me.flibio.minigamecore.arena;

import me.flibio.minigamecore.events.ArenaStateChangeEvent;

import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Arena {
	
	public enum DefaultArenaState {
		LOBBY_WAITING("LOBBY_WAITING"),LOBBY_COUNTDOWN("LOBBY_COUNTDOWN"),COUNTDOWN_CANCELLED("COUNTDOWN_CANCELLED"),
		GAME_COUNTDOWN("GAME_COUNTDOWN"),GAME_PLAYING("GAME_PLAYING"),GAME_OVER("GAME_OVER");
		
		private ArenaState state;
		
		DefaultArenaState(String stateName) {
			state = ArenaStateBuilder.createBasicArenaState(stateName);
		}
		
		public ArenaState getState() {
			return state;
		}
	}

	private CopyOnWriteArrayList<ArenaState> arenaStates = new CopyOnWriteArrayList<ArenaState>(getDefaultArenaStates());
	private ConcurrentHashMap<ArenaState,Runnable> runnables = new ConcurrentHashMap<ArenaState,Runnable>();
	
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
		this.arenaState = DefaultArenaState.LOBBY_WAITING.getState();
		
		game.getEventManager().registerListeners(plugin, this);
	}
	
	/**
	 * Adds an online player
	 * @param player
	 * 	The player to add
	 */
	public void addOnlinePlayer(Player player) {
		//Check if the game is in the correct state
		if(arenaState.equals(DefaultArenaState.LOBBY_WAITING.getState())||arenaState.equals(DefaultArenaState.LOBBY_COUNTDOWN.getState())) {
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
				if(arenaState.equals(DefaultArenaState.LOBBY_WAITING.getState())&&onlinePlayers.size()>=arenaOptions.getMinPlayers()) {
					arenaStateChange(DefaultArenaState.LOBBY_COUNTDOWN.getState());
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
	}
	
	/**
	 * Removes an online player
	 * @param player
	 * 	The player to remove
	 */
	public void removeOnlinePlayer(Player player) {
		onlinePlayers.remove(player);
		if(arenaState.equals(DefaultArenaState.LOBBY_COUNTDOWN.getState())&&onlinePlayers.size()<arenaOptions.getMinPlayers()) {
			arenaStateChange(ArenaStateBuilder.createBasicArenaState("COUNTDOWN_CANCELLED"));
		}
		for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
			//TODO - replace %name% with the player name
			onlinePlayer.sendMessage(arenaOptions.playerQuit);
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
		//Run a runnable ifit is set
		if(arenaStateRunnableExists(changeTo)) {
			runnables.get(changeTo).run();
		}
		//Run default actions ifthey are enabled
		if(arenaOptions.isDefaultStateChangeActions()) {
			if(arenaState.getStateName().equalsIgnoreCase("LOBBY_COUNTDOWN")) {
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
							arenaStateChange(ArenaStateBuilder.createBasicArenaState("GAME_COUNTDOWN"));
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
			} else if(arenaState.getStateName().equalsIgnoreCase("COUNTDOWN_CANCELLED")) {
				currentLobbyCountdown = arenaOptions.getLobbyCountdownTime();
				if(lobbyCountdownTask!=null) {
					lobbyCountdownTask.cancel();
				}
				arenaState = ArenaStateBuilder.createBasicArenaState("LOBBY_WAITING");
				for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
					onlinePlayer.sendMessage(arenaOptions.lobbyCountdownCancelled);
				}
			} else if(arenaState.getStateName().equalsIgnoreCase("GAME_COUNTDOWN")) {
				currentLobbyCountdown = arenaOptions.getLobbyCountdownTime();
				//Send a message
				for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
					onlinePlayer.sendMessage(arenaOptions.gameStarting);
				}
				//TODO - Delay before game starts
				arenaState = ArenaStateBuilder.createBasicArenaState("GAME_PLAYING");
			} else if(arenaState.getStateName().equalsIgnoreCase("GAME_OVER")) {
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
			return Optional.absent();
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
			return Optional.absent();
		}
		//TODO
		return Optional.absent();
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
	 * @param arenaState
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
		return Arrays.asList(DefaultArenaState.LOBBY_WAITING.getState(),DefaultArenaState.LOBBY_COUNTDOWN.getState(),
				DefaultArenaState.GAME_COUNTDOWN.getState(),DefaultArenaState.GAME_PLAYING.getState(),
				DefaultArenaState.GAME_OVER.getState(),DefaultArenaState.COUNTDOWN_CANCELLED.getState());
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
			return Optional.absent();
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

	//Listeners
	
	@Listener
	public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
		if(arenaOptions.isDedicatedServer()&&arenaOptions.isTriggerPlayerEvents()) {
			Player player = event.getTargetEntity();
			removeOnlinePlayer(player);
		}
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		if(arenaOptions.isDedicatedServer()&&arenaOptions.isTriggerPlayerEvents()) {
			Player player = event.getTargetEntity();
			addOnlinePlayer(player);
		}
	}
}
