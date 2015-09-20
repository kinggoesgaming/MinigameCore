package me.Flibio.MinigameCore.Kits;

/*
 * IMPLEMENTATION TODO LIST
 * 
 * ---
 * 
 * METHOD TODO LIST
 * 
 * addItem
 * removeItem
 * getItems
 * setName
 * getCost
 * setCost
 */
public class Kit {
	
	private String name;
	
	public Kit(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of the kit
	 * @return
	 * 	The name of the kit
	 */
	public String getName() {
		return name;
	}
}
