package me.flibio.minigamecore.main;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

@Plugin(id = "MinigameCore", name = "MinigameCore", version = "1.0.0")
public class MinigameCore {
	
	@Inject
	private Logger logger;
	
	@Inject
	private Game game;
	
	public static MinigameCore access;
	
	@Listener
	public void onServerInitialize(GamePreInitializationEvent event) {
		access = this;
		logger.info("Initializing MinigameCore...");
	}
	
	public Logger getLogger() {
		return this.logger;
	}
	
	public Game getGame() {
		return this.game;
	}
}
