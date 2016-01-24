package me.flibio.minigamecore.arena;

public class ArenaOptions {
	
	//Text Customization Options - Supports XML formatting
	public String gameInProgress = "<c n=\"red\">The game is currently in progress!</c>";
	public String lobbyFull = "<c n=\"red\">The lobby is full!</c>";
	public String playerJoined = "<c n=\"yellow\">%name%</c><c n=\"gray\"> has joined the game!</c>";
	public String playerQuit = "<c n=\"yellow\">%name%</c><c n=\"gray\"> has left the game!</c>";
	public String lobbyCountdownStarted = "<c n=\"yellow\">%time%</c><c n=\"gray\"> until the game begins!</c>";
	public String lobbyCountdownCancelled = "<c n=\"red\">Countdown cancelled!</c>";
	public String lobbyCountdownProgress = "<c n=\"yellow\">%time%</c><c n=\"gray\"> until the game begins!</c>";
	public String gameStarting = "<c n=\"green\">The game is starting in </c><c n=\"yellow\">%time%</c><c n=\"green\"> seconds!</c>";
	public String gameOver = "<c n=\"green\">The game is over!</c>";
	
	private boolean dedicatedServer = true;
	private int minPlayers = 2;
	private int maxPlayers = 8;
	private int lobbyCountdownTime = 30;
	private String name = "";
	private boolean endGameDelay = true;
	private boolean endGameSpectator = true;
	private boolean triggerPlayerEvents = true;
	private boolean defaultStateChangeActions = true;
	private boolean defaultPlayerEventActions = true;
	private boolean modifyLobbyBlocks = false;
	private boolean allowLobbyDamage = false;
	
	/**
	 * Stores configurable options for the arena
	 * @param arenaName
	 * 	The name of the arena
	 */
	public ArenaOptions(String arenaName) {
		this.name = arenaName;
	}

	/**
	 * Checks if the arena is set to dedicated server mode
	 * @return
	 * 	If the server is in dedicated server mode
	 */
	public boolean isDedicatedServer() {
		return dedicatedServer;
	}

	/**
	 * Changes the dedicated server mode
	 * @param dedicatedServer
	 * 	What to set dedicated server mode to
	 */
	public void setDedicatedServer(boolean dedicatedServer) {
		this.dedicatedServer = dedicatedServer;
	}

	/**
	 * Gets the minimum players to start the game
	 * @return
	 * 	The amount of minimum players required to start the game
	 */
	public int getMinPlayers() {
		return minPlayers;
	}

	/**
	 * Sets the amount of minimum players required to start the game
	 * @param minPlayers
	 * 	The amount of players required to start the game
	 */
	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	/**
	 * Gets the maximum players allowed in a game
	 * @return
	 * 	The amount of maximum players allowed in a game
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * Sets the amount of maximum players allowed in a game
	 * @param maxPlayers
	 * 	The amount of maximum players allowed in a game
	 */
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	/**
	 * Gets the amount of time the lobby will countdown when
	 * the minimum player requirements have been met
	 * @return
	 * 	The lobby countdown time
	 */
	public int getLobbyCountdownTime() {
		return lobbyCountdownTime;
	}

	/**
	 * Sets the amount of time the lobby will countdown when
	 * the minimum player requirements have been met
	 * @param lobbyCountdownTime
	 * 	The lobby countdown time to set
	 */
	public void setLobbyCountdownTime(int lobbyCountdownTime) {
		this.lobbyCountdownTime = lobbyCountdownTime;
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
	 * Checks if the game will wait 5 seconds after the game ends before 
	 * teleporting/kicking
	 * @return
	 * 	If the game will wait 5 seconds after the game ends before 
	 * teleporting/kicking
	 */
	public boolean isEndGameDelay() {
		return endGameDelay;
	}

	/**
	 * Sets if the game will wait 5 seconds after the game ends before 
	 * teleporting/kicking
	 * @param endGameDelay
	 * 	If the game will wait 5 seconds after the game ends before 
	 * teleporting/kicking
	 */
	public void setEndGameDelay(boolean endGameDelay) {
		this.endGameDelay = endGameDelay;
	}

	/**
	 * Checks if the game will set players to spectator mode while
	 * waiting 5 seconds after the game ends
	 * @return
	 *  If the game will set players to spectator mode while waiting 5 seconds 
	 *  after the game ends
	 */
	public boolean isEndGameSpectator() {
		return endGameSpectator;
	}

	/**
	 * Sets if the game will set players to spectator mode while
	 * waiting 5 seconds after the game ends
	 * @param endGameSpectator
	 * 	If the game will set players to spectator mode while waiting 5 seconds 
	 * after the game ends
	 */
	public void setEndGameSpectator(boolean endGameSpectator) {
		this.endGameSpectator = endGameSpectator;
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
	 * Checks if MinigameCore will run default state change actions
	 * @return
	 * 	If MinigameCore will run default state change actions
	 */
	public boolean isDefaultStateChangeActions() {
		return defaultStateChangeActions;
	}

	/**
	 * Sets if MinigameCore will run default state change actions
	 * @param defaultStateChangeActions
	 * 	If MinigameCore will run default state change actions
	 */
	public void setDefaultStateChangeActions(boolean defaultStateChangeActions) {
		this.defaultStateChangeActions = defaultStateChangeActions;
	}

	/**
	 * Checks if MinigameCore will run default player event actions
	 * @return
	 * 	If MinigameCore will run default player event actions
	 */
	public boolean isDefaultPlayerEventActions() {
		return defaultPlayerEventActions;
	}

	/**
	 * Sets if MinigameCore will run default player event actions
	 * @param defaultPlayerEventActions
	 * 	If MinigameCore will run default player event actions
	 */
	public void setDefaultPlayerEventActions(boolean defaultPlayerEventActions) {
		this.defaultPlayerEventActions = defaultPlayerEventActions;
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
	
}
