package me.flibio.minigamecore.arena;

/**
 * Different states an arena can be in
 */
public class ArenaStates {
	
	/**
	 * Wating for the minimum amount of players to join
	 */
	public static final ArenaState LOBBY_WAITING = new ArenaState("LOBBY_WAITING");
	/**
	 * Counting down until the game begins
	 */
	public static final ArenaState LOBBY_COUNTDOWN = new ArenaState("LOBBY_COUNTDOWN");
	/**
	 * No longer minimum amount of players present, the countdown was cancelled
	 */
	public static final ArenaState COUNTDOWN_CANCELLED = new ArenaState("COUNTDOWN_CANCELLED");
	/**
	 * Players are in the arena, waiting for the game to begin
	 */
	public static final ArenaState GAME_COUNTDOWN = new ArenaState("GAME_COUNTDOWN");
	/**
	 * The game is currently in progress
	 */
	public static final ArenaState GAME_PLAYING = new ArenaState("GAME_PLAYING");
	/**
	 * The game has ended and players will return to the lobby
	 */
	public static final ArenaState GAME_OVER = new ArenaState("GAME_OVER");
	
}
