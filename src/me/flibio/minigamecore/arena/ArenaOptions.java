package me.flibio.minigamecore.arena;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class ArenaOptions {
	
	//Text Customization Options
	public Text gameInProgress = Texts.builder("The game is currently in progress!")
			.color(TextColors.RED).build();
	public Text lobbyFull = Texts.builder("The lobby is full!").color(TextColors.RED).build();
	public Text playerJoined = Texts.builder("%name%").color(TextColors.YELLOW)
			.append(Texts.builder(" has joined the game!").color(TextColors.GRAY).build()).build();
	public Text lobbyCountdownStarted = Texts.builder("%time%").color(TextColors.YELLOW)
			.append(Texts.builder(" until the game begins!")
			.color(TextColors.GRAY).build()).build();
	public Text lobbyCountdownCancelled = Texts.builder("Countdown cancelled!")
			.color(TextColors.RED).build();
	public Text lobbyCountdownProgress = Texts.builder("%time%").color(TextColors.YELLOW)
			.append(Texts.builder(" until the game begins!")
			.color(TextColors.GRAY).build()).build();
	public Text gameStarting = Texts.builder("The game is starting in 5 seconds!")
			.color(TextColors.GREEN).build();
	public Text gameOver = Texts.builder("The game is over!").color(TextColors.GREEN).build();
	
	private boolean dedicatedServer = true;
	private int minPlayers = 0;
	private int maxPlayers = 0;
	private int lobbyCountdownTime = 30;
	private String name = "";
	private boolean endGameDelay = true;
	private boolean endGameSpectator = true;
	
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
	 * Checks if the game will wait 5 seconds after the game ends before teleporting/kicking
	 * @return
	 * 	If the game will wait 5 seconds after the game ends before teleporting/kicking
	 */
	public boolean isEndGameDelay() {
		return endGameDelay;
	}

	/**
	 * Sets if the game will wait 5 seconds after the game ends before teleporting/kicking
	 * @param endGameDelay
	 * 	If the game will wait 5 seconds after the game ends before teleporting/kicking
	 */
	public void setEndGameDelay(boolean endGameDelay) {
		this.endGameDelay = endGameDelay;
	}

	/**
	 * Checks if the game will set players to spectator mode while
	 * waiting 5 seconds after the game ends
	 * @return
	 *  If the game will set players to spectator mode while waiting 5 seconds after the game ends
	 */
	public boolean isEndGameSpectator() {
		return endGameSpectator;
	}

	/**
	 * Sets if the game will set players to spectator mode while
	 * waiting 5 seconds after the game ends
	 * @param endGameSpectator
	 * 	If the game will set players to spectator mode while waiting 5 seconds after the game ends
	 */
	public void setEndGameSpectator(boolean endGameSpectator) {
		this.endGameSpectator = endGameSpectator;
	}
	
}
