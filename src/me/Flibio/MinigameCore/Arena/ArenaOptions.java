package me.Flibio.MinigameCore.Arena;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class ArenaOptions {
	
	//Text Customization Options
	public Text GAME_IN_PROGRESS = Texts.builder("The game is currently in progress!").color(TextColors.RED).build();
	public Text LOBBY_FULL = Texts.builder("The lobby is full!").color(TextColors.RED).build();
	public Text PLAYER_JOINED = Texts.builder("%name%").color(TextColors.YELLOW).append(Texts.builder(" has joined the game!").color(TextColors.GRAY).build()).build();
	public Text LOBBY_COUNTDOWN_STARTED = Texts.builder("%time%").color(TextColors.YELLOW).append(Texts.builder(" until the game begins!").color(TextColors.GRAY).build()).build();
	public Text LOBBY_COUNTDOWN_CANCELLED = Texts.builder("Countdown cancelled!").color(TextColors.RED).build();
	public Text LOBBY_COUNTDOWN_PROGRESS = Texts.builder("%time%").color(TextColors.YELLOW).append(Texts.builder(" until the game begins!").color(TextColors.GRAY).build()).build();
	public Text GAME_STARTING = Texts.builder("The game is starting in 5 seconds!").color(TextColors.GREEN).build();
	public Text GAME_OVER = Texts.builder("The game is over!").color(TextColors.GREEN).build();
	
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
	 * @return the dedicatedServer
	 */
	public boolean isDedicatedServer() {
		return dedicatedServer;
	}

	/**
	 * @param dedicatedServer the dedicatedServer to set
	 */
	public void setDedicatedServer(boolean dedicatedServer) {
		this.dedicatedServer = dedicatedServer;
	}

	/**
	 * @return the minPlayers
	 */
	public int getMinPlayers() {
		return minPlayers;
	}

	/**
	 * @param minPlayers the minPlayers to set
	 */
	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	/**
	 * @return the maxPlayers
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * @param maxPlayers the maxPlayers to set
	 */
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	/**
	 * @return the lobbyCountdownTime
	 */
	public int getLobbyCountdownTime() {
		return lobbyCountdownTime;
	}

	/**
	 * @param lobbyCountdownTime the lobbyCountdownTime to set
	 */
	public void setLobbyCountdownTime(int lobbyCountdownTime) {
		this.lobbyCountdownTime = lobbyCountdownTime;
	}

	/**
	 * @return the arena name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the endGameDelay
	 */
	public boolean isEndGameDelay() {
		return endGameDelay;
	}

	/**
	 * @param endGameDelay the endGameDelay to set
	 */
	public void setEndGameDelay(boolean endGameDelay) {
		this.endGameDelay = endGameDelay;
	}

	/**
	 * @return the endGameSpectator
	 */
	public boolean isEndGameSpectator() {
		return endGameSpectator;
	}

	/**
	 * @param endGameSpectator the endGameSpectator to set
	 */
	public void setEndGameSpectator(boolean endGameSpectator) {
		this.endGameSpectator = endGameSpectator;
	}
	
}
