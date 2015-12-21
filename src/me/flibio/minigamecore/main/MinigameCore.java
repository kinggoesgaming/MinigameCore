package me.flibio.minigamecore.main;

import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

@Plugin(id = "MinigameCore", name = "MinigameCore", version = "1.0.0")
public class MinigameCore {
	
	@Inject
	Logger logger;
	
	@Listener
	public void onServerInitialize(GamePreInitializationEvent event) {
		logger.info("Initializing MinigameCore Build #${BUILD_NUMBER}");
	}
}
