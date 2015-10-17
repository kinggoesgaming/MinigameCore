package me.flibio.minigamecore.arena;

public class ArenaStates {
	
	public static final ArenaState LOBBY_WAITING = new ArenaState("LOBBY_WAITING");
	public static final ArenaState LOBBY_COUNTDOWN = new ArenaState("LOBBY_COUNTDOWN");
	public static final ArenaState COUNTDOWN_CANCELLED = new ArenaState("COUNTDOWN_CANCELLED");
	public static final ArenaState GAME_COUNTDOWN = new ArenaState("GAME_COUNTDOWN");
	public static final ArenaState GAME_PLAYING = new ArenaState("GAME_PLAYING");
	public static final ArenaState GAME_OVER = new ArenaState("GAME_OVER");
	
}
