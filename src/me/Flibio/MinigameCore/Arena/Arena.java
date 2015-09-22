package me.Flibio.MinigameCore.Arena;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Arena {
	
	public enum ArenaState {
		LOBBY_WAITING,
		LOBBY_COUNTDOWN,
		GAME_COUNTDOWN,
		GAME_PLAYING,
		GAME_OVER
	}
	
	private Text GAME_IN_PROGRESS = Texts.builder("The game is currently in progress!").color(TextColors.RED).build();
	private Text LOBBY_FULL = Texts.builder("The lobby is full!").color(TextColors.RED).build();
	private Text PLAYER_JOINED = Texts.builder("%name%").color(TextColors.YELLOW).append(Texts.builder(" has joined the game!").color(TextColors.GRAY).build()).build();
	private Text LOBBY_COUNTDOWN_STARTED = Texts.builder("%time%").color(TextColors.YELLOW).append(Texts.builder(" until the game begins!").color(TextColors.GRAY).build()).build();
	private Text LOBBY_COUNTDOWN_CANCELLED = Texts.builder("Countdown cancelled!").color(TextColors.RED).build();
	private Text LOBBY_COUNTDOWN_PROGRESS = Texts.builder("%time%").color(TextColors.YELLOW).append(Texts.builder(" until the game begins!").color(TextColors.GRAY).build()).build();
	private Text GAME_STARTING = Texts.builder("The game is starting in 5 seconds!").color(TextColors.GREEN).build();
	private Text GAME_OVER = Texts.builder("The game is over!").color(TextColors.GREEN).build();
	
	private Location<World> lobbySpawnLocation;
	private Location<World> failedJoinLocation;
	private ConcurrentHashMap<String, Location<World>> spawnLocations = new ConcurrentHashMap<String, Location<World>>();
	private CopyOnWriteArrayList<Player> onlinePlayers = new CopyOnWriteArrayList<Player>();
	private String arenaName;
	private int minPlayers = 0;
	private int maxPlayers = 0;
	private Runnable runnable;
	private Runnable onGameStart;
	private ArenaState arenaState;
	private int lobbyCountdown = 30;
	private int currentLobbyCountdown;
	private Task lobbyCountdownTask;
	
	private boolean dedicatedServer;
	
	private Game game;
	private Object plugin;
	
	//TODO - Scoreboard implementation throughout arena
	public Arena(String arenaName, Game game, Runnable runnable, Object plugin, boolean dedicatedServer, Location<World> lobbySpawnLocation) {
		this.arenaName = arenaName;
		this.game = game;
		this.runnable = runnable;
		this.dedicatedServer = dedicatedServer;
		this.lobbySpawnLocation = lobbySpawnLocation;
		
		game.getEventManager().registerListeners(plugin, this);
	}
	
	public void addOnlinePlayer(Player player) {
		//Check if the game is in the correct state
		if(arenaState.equals(ArenaState.LOBBY_WAITING)||arenaState.equals(ArenaState.LOBBY_COUNTDOWN)) {
			if(onlinePlayers.size()>=maxPlayers) {
				//Lobby is full
				if(dedicatedServer) {
					//Kick the player
					player.kick(LOBBY_FULL);
				} else {
					//Try to teleport the player to the failed join location
					if(failedJoinLocation!=null) {
						player.sendMessage(LOBBY_FULL);
						player.setLocation(failedJoinLocation);
					} else {
						player.kick(LOBBY_FULL);
					}
				}
			} else {
				//Player can join
				onlinePlayers.add(player);
				for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
					//TODO - replace %name% with the player name
					onlinePlayer.sendMessage(PLAYER_JOINED);
				}
				player.setLocation(lobbySpawnLocation);
			}
		} else {
			if(dedicatedServer) {
				//Kick the player
				player.kick(GAME_IN_PROGRESS);
			} else {
				//Try to teleport the player to the failed join location
				if(failedJoinLocation!=null) {
					player.sendMessage(GAME_IN_PROGRESS);
					player.setLocation(failedJoinLocation);
				} else {
					player.kick(GAME_IN_PROGRESS);
				}
			}
		}
	}
	
	public void removeOnlinePlayer(Player player) {
		/*TODO
		 * Disconnect Message
		 */
		onlinePlayers.remove(player);
	}
	
	public CopyOnWriteArrayList<Player> getOnlinePlayers() {
		return onlinePlayers;
	}
	
	
	/**
	 * Initializes the arena. This starts the game clock with the runnable provided in the Arena constructor. The runnable runs on a seperate thread every 1 tick.
	 */
	public void initializeArena() {
		//TODO - Make a boolean and do checks
		game.getScheduler().createTaskBuilder().execute(new Runnable() {
			public void run() {
				if(onlinePlayers.size()<minPlayers&&arenaState.equals(ArenaState.LOBBY_COUNTDOWN)) {
					//Cancel the countdown
					cancelCountdown();
				}
				if(onlinePlayers.size()>=minPlayers&&arenaState.equals(ArenaState.LOBBY_WAITING)) {
					//Start the countdown
					startCountdown();
				}
				runnable.run();
			}
		}).async().interval(1).submit(plugin);
	}
	
	//Lobby Control Methods
	
	private void startCountdown() {
		currentLobbyCountdown = lobbyCountdown;
		arenaState = ArenaState.LOBBY_COUNTDOWN;
		for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
			//TODO - replace %time% with the seconds to go
			onlinePlayer.sendMessage(LOBBY_COUNTDOWN_STARTED);
		}
		//Register task the run the countdown every 1 second
		lobbyCountdownTask = game.getScheduler().createTaskBuilder().execute(new Runnable() {
			public void run() {
				if(currentLobbyCountdown==0) {
					startGame();
					lobbyCountdownTask.cancel();
					return;
				}
				if(lobbyCountdown/2==currentLobbyCountdown||currentLobbyCountdown<=10) {
					//Send a message
					for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
						//TODO - replace %time% with the seconds to go
						onlinePlayer.sendMessage(LOBBY_COUNTDOWN_PROGRESS);
					}
				}
				currentLobbyCountdown--;
			}
		}).async().interval(20).submit(plugin);
	}
	
	private void cancelCountdown() {
		currentLobbyCountdown = lobbyCountdown;
		if(lobbyCountdownTask!=null) {
			lobbyCountdownTask.cancel();
		}
		arenaState = ArenaState.LOBBY_WAITING;
		for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
			onlinePlayer.sendMessage(LOBBY_COUNTDOWN_CANCELLED);
		}
	}
	
	//Game Control Methods
	
	private void startGame() {
		currentLobbyCountdown = lobbyCountdown;
		arenaState = ArenaState.GAME_COUNTDOWN;
		//Send a message
		for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
			onlinePlayer.sendMessage(GAME_STARTING);
		}
		if(onGameStart!=null) {
			onGameStart.run();
		}
	}
	
	public void endGame() {
		arenaState = ArenaState.GAME_OVER;
		for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
			onlinePlayer.sendMessage(GAME_OVER);
			//TODO - Save the gamemode and load again after 5 seconds
			onlinePlayer.offer(Keys.GAME_MODE,GameModes.SPECTATOR);
		}
		game.getScheduler().createTaskBuilder().execute(new Runnable() {
			public void run() {
				for(Player onlinePlayer : game.getServer().getOnlinePlayers()) {
					onlinePlayer.sendMessage(GAME_OVER);
					onlinePlayer.offer(Keys.GAME_MODE,GameModes.SURVIVAL);
					if(dedicatedServer) {
						onlinePlayer.kick(GAME_OVER);
					} else {
						onlinePlayer.setLocation(lobbySpawnLocation);
					}
				}
			}
		}).async().delay(5,TimeUnit.SECONDS).submit(plugin);
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
	
	//Other Arena Properties
	
	/**
	 * Sets the location a player will teleport to if they failed to join the arena(Non-Dedicated Arenas Only)
	 * @param location
	 * 	The location to set the failed join location to
	 */
	public void setFailedJoinLocation(Location<World> location) {
		this.failedJoinLocation = location;
	}
	
	/**
	 * Sets how long a lobby will wait for when the lobby reaches the minimum amount of players
	 * @param lobbyCountdown
	 * 	The amount of seconds a lobby will countdown for
	 */
	public void setLobbyCountdownTime(int lobbyCountdown) {
		this.lobbyCountdown = lobbyCountdown;
	}
	
	/**
	 * Gets the minimum amount of players in a lobby
	 * @return
	 */
	public int getMinPlayers() {
		return minPlayers;
	}
	
	/**
	 * Sets the minimum amount of players allowed in an arena
	 * @param minPlayers
	 * 	The minimum amount of players allowed in the arena
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean setMinPlayers(int minPlayers) {
		if(minPlayers<0) {
			return false;
		}
		this.minPlayers = minPlayers;
		return true;
	}
	
	/**
	 * Sets the maximum amount of players allowed in an arena
	 * @param maxPlayers
	 * 	The maximum players allowed in the arena
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean setMaxPlayers(int maxPlayers) {
		if(maxPlayers<0) {
			return false;
		}
		this.maxPlayers = maxPlayers;
		return true;
	}
	
	/**
	 * Gets the maxiumum amount of players allowed in a lobby
	 * @return
	 * 	The maximum amount of players in a lobby
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	/**
	 * Gets the name of the arena
	 * @return
	 * 	The name of the arena
	 */
	public String getName() {
		return arenaName;
	}
	
	//Listeners
	
	@Listener
	public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
		if(dedicatedServer) {
			Player player = event.getTargetEntity();
			removeOnlinePlayer(player);
		}
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		if(dedicatedServer) {
			Player player = event.getTargetEntity();
			addOnlinePlayer(player);
		}
	}
}
