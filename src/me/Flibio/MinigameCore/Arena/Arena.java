package me.Flibio.MinigameCore.Arena;

import java.util.concurrent.CopyOnWriteArrayList;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Arena {
	
	private CopyOnWriteArrayList<Location<World>> spawnLocations = new CopyOnWriteArrayList<Location<World>>();
	private String arenaName;
	private int minPlayers = 0;
	private int maxPlayers = 0;
	
	public Arena(String arenaName) {
		this.arenaName = arenaName;
	}
	
	public int getMinPlayers() {
		return minPlayers;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public String getArenaName() {
		return arenaName;
	}
	
	public CopyOnWriteArrayList<Location<World>> getSpawnLocations() {
		return spawnLocations;
	}

}
