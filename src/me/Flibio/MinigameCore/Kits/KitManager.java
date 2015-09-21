package me.Flibio.MinigameCore.Kits;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class KitManager {
	
	private CopyOnWriteArrayList<Kit> kits = new CopyOnWriteArrayList<Kit>();
	
	public KitManager() {
		
	}
	
	/**
	 * Registers a new kit with the kit manager
	 * @param kit
	 * 	The kit to register
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean registerKit(Kit kit) {
		//TODO
		return false;
	}
	
	/**
	 * Removes a kit from the kit manager
	 * @param name
	 * 	Name of the kit to remove
	 * @return
	 * 	Boolean based on if the method was successful or not
	 */
	public boolean removeKit(String name) {
		//TODO
		return false;
	}
	
	/**
	 * Gets a kit from the kit manager
	 * @param name
	 * 	The name of the kit to get
	 * @return
	 * 	Optional of the kit
	 */
	public Optional<Kit> getKit(String name) {
		for(Kit kit : kits) {
			if(kit.getName().equalsIgnoreCase(name)) {
				return Optional.of(kit);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if a kit exists
	 * @param name
	 * 	The name of the kit to check for
	 * @return
	 * 	Boolean based on if the kit was found or not
	 */
	public boolean kitExists(String name) {
		for(Kit kit : kits) {
			if(kit.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}
