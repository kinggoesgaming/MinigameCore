package me.Flibio.MinigameCore.Main;

import me.Flibio.MinigameCore.Arena.ArenaManager;
import me.Flibio.MinigameCore.Economy.EconomyManager;
import me.Flibio.MinigameCore.FileManagement.FileManager;
import me.Flibio.MinigameCore.Kits.KitManager;
import me.Flibio.MinigameCore.Scoreboards.ScoreboardManager;

import org.spongepowered.api.Game;

public class Minigame {
	
	//Managers
	private ArenaManager arenaManager;
	private EconomyManager economyManager;
	private FileManager fileManager;
	private KitManager kitManager;
	private ScoreboardManager scoreboardManager;

	private String name;

	private Game game;
	
	public Minigame(String name, Game game) {
		this.name = name;
		this.game = game;
		
		//Initialize Managers
		this.arenaManager = new ArenaManager();
		this.economyManager = new EconomyManager(this.game);
		this.fileManager = new FileManager();
		this.kitManager = new KitManager();
		this.scoreboardManager = new ScoreboardManager(this.game);
	}
	
	/**
	 * Get the name of the minigame
	 * @return
	 * 	The name of the minigame
	 */
	public String getName() {
		return name;
	}
	
	//All manager getters
	public ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	public EconomyManager getEconomyManager() {
		return economyManager;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public KitManager getKitManager() {
		return kitManager;
	}
	
	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}
}
