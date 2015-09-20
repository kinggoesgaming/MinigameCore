package me.Flibio.MinigameCore.Main;

import me.Flibio.MinigameCore.Arena.ArenaManager;
import me.Flibio.MinigameCore.Economy.EconomyManager;
import me.Flibio.MinigameCore.FileManagement.FileManager;
import me.Flibio.MinigameCore.Kits.KitManager;

public class Minigame {
	
	//Managers
	private ArenaManager arenaManager;
	private EconomyManager economyManager;
	private FileManager fileManager;
	private KitManager kitManager;
	
	//Minigame properties
	private String name;
	
	public Minigame(String name) {
		this.name = name;
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
}
