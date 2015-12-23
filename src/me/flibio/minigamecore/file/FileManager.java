package me.flibio.minigamecore.file;

import me.Flibio.EconomyLite.Main;
import me.flibio.minigamecore.arena.Arena;
import me.flibio.minigamecore.arena.ArenaOptions;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager {
	
	private ConcurrentHashMap<String, ConfigurationNode> files = new ConcurrentHashMap<String, ConfigurationNode>();
	
	private Logger logger;
	private String name;
	
	/**
	 * Provides easy-to-use file configuration and data storage
	 * @param logger
	 * 	An instance of the logger
	 * @param name
	 * 	The name of the game
	 */
	public FileManager(Logger logger, String name) {
		this.logger = logger;
		this.name = name;
	}
	
	/**
	 * Generates a folder with a specified name
	 * @param name
	 * 	The name of the folder to generate
	 * @return
	 * 	If the method was successful or not
	 */
	private boolean generateFolder(String name) {
		File folder = new File("config/"+name);
		try {
			if(!folder.exists()) {
				if(folder.mkdir()) {
					logger.info("Successfully generated folder "+name);
					return true;
				} else {
					logger.error("Error generating folder "+name);
					return false;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.error("Error generating folder: "+e.getMessage());
			return false;
		}
	}
	
	/**
	 * Initializes a file into the file manager. This must be done each server start.
	 * @param fileName
	 * 	The name of the file to initialize
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean initializeFile(String fileName) {
		if(fileName.contains(".conf")) {
			fileName = fileName.replace(".conf", "").trim();
		}
		if(generateFolder(this.name)) {
			if(!files.containsKey(fileName)) {
				//File isn't registered
				File file = new File("config/"+this.name+"/"+fileName+".conf");
				if(!file.exists()) {
					//File doesn't exist
					try {
						file.createNewFile();
						ConfigurationLoader<?> manager = HoconConfigurationLoader.builder()
								.setFile(new File("config/"+this.name+"/"+fileName+".conf")).build();
						ConfigurationNode root;
						root = manager.load();
						files.put(fileName, root);
						return true;
					} catch (IOException e) {
						logger.error("Error initializing file "+fileName+": "+e.getMessage());
						return false;
					}
				} else {
					//File already exists
					ConfigurationLoader<?> manager = HoconConfigurationLoader.builder()
							.setFile(new File("config/"+this.name+"/"+fileName+".conf")).build();
					ConfigurationNode root;
					//Try to load the file
					try {
						root = manager.load();
						files.put(fileName, root);
						return true;
					} catch (IOException e) {
						logger.error("Error initializing file "+fileName+": "+e.getMessage());
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a default option to the specified file. Allows for "." seperators 
	 * in the key path. If the specified path has no value set, method will 
	 * set it to the default value specified.
	 * @param fileName
	 * 	The name of the file
	 * @param key
	 * 	The path of the default option
	 * @param value
	 * 	The default value of the option
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean defaultOption(String fileName, String key, Object value) {
		if(fileName.contains(".conf")) {
			fileName = fileName.replace(".conf", "").trim();
		}
		Optional<ConfigurationNode> fileOptional = getFile(fileName);
		if(fileOptional.isPresent()) {
			ConfigurationNode file = fileOptional.get();
			if(file.getNode((Object[]) key.split("\\.")).getValue()==null) {
				file.getNode((Object[]) key.split("\\.")).setValue(value);
				saveFile(fileName,file);
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Saves a file. Writes to disk and updates the list of files.
	 * @param fileName
	 * 	The name of the file to save
	 * @param fileData
	 * 	The file data to be saved
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean saveFile(String fileName, ConfigurationNode fileData) {
		if(fileName.contains(".conf")) {
			fileName = fileName.replace(".conf", "").trim();
		}
		File file = new File("config/"+this.name+"/"+fileName+".conf");
		if(file.exists()) {
			ConfigurationLoader<?> manager = HoconConfigurationLoader.builder().setFile(file).build();
			try {
				manager.save(fileData);
				return true;
			} catch (IOException e) {
				logger.error("Error saving "+fileName+": "+e.getMessage());
				return false;
			}
		} else {
			//File doesn't exist
			return false;
		}
	}
	
	/**
	 * Gets an initialized file
	 * @param name
	 * 	The name of the file to get
	 * @return
	 * 	The ConfigurationNode of the file
	 */
	public Optional<ConfigurationNode> getFile(String name) {
		if(name.contains(".conf")) {
			name = name.replace(".conf", "").trim();
		}
		if(files.containsKey(name)) {
			return Optional.of(files.get(name));
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Saves an arena to the file
	 * @param arena
	 * 	The arena to save
	 * @param fileName
	 * 	The location to save the arena
	 * @return
	 * 	If arena was saved successfully or not
	 */
	public boolean saveArena(Arena arena, String fileName) {
		initializeFile(fileName);
		Optional<ConfigurationNode> fopt = getFile(fileName);
		if(fopt.isPresent()) {
			ConfigurationNode file = fopt.get();
			ArenaOptions options = arena.getOptions();
			String arenaName = options.getName();
			ConfigurationNode arenaNode = file.getNode(arenaName);
			file.setValue(null);
			arenaNode.getNode("minPl").setValue(options.getMinPlayers());
			arenaNode.getNode("maxPl").setValue(options.getMaxPlayers());
			arenaNode.getNode("lobbyCtdnTime").setValue(options.getLobbyCountdownTime());
			arenaNode.getNode("dedicated").setValue(options.isDedicatedServer());
			arenaNode.getNode("dfltPlEvActions").setValue(options.isDefaultPlayerEventActions());
			arenaNode.getNode("dfltStChActions").setValue(options.isDefaultStateChangeActions());
			arenaNode.getNode("endGameDelay").setValue(options.isEndGameDelay());
			arenaNode.getNode("endGameSpec").setValue(options.isEndGameSpectator());
			arenaNode.getNode("triggerPlEvents").setValue(options.isTriggerPlayerEvents());
			if(arena.getLobbySpawnLocation().isPresent()) {
				saveLoc(arenaNode.getNode("lobbySpawn"),arena.getLobbySpawnLocation().get());
			}
			if(arena.getFailedJoinLocation().isPresent()) {
				saveLoc(arenaNode.getNode("failedJoin"),arena.getFailedJoinLocation().get());
			}
			for(String spawn : arena.getSpawnLocations().keySet()) {
				saveLoc(arenaNode.getNode("spawns").getNode(spawn),arena.getSpawnLocations().get(spawn));
			}
			for(String key : arena.getCustomLocations().keySet()) {
				saveLoc(arenaNode.getNode("customLoc").getNode(key),arena.getCustomLocations().get(key));
			}
			for(String key : arena.getCustomVariables().keySet()) {
				arenaNode.getNode("custom").getNode(key).setValue(arena.getCustomVariables().get(key));
			}
			return saveFile(fileName,file);
		} else {
			return false;
		}
	}
	
	/**
	 * Loads all arenas from a specified file
	 * @param fileName
	 * 	The file to load arenas from
	 * @return
	 * 	All arenas found in the file
	 */
	public ArrayList<Arena> loadArenas(String fileName) {
		initializeFile(fileName);
		Optional<ConfigurationNode> fopt = getFile(fileName);
		ArrayList<Arena> arenas = new ArrayList<Arena>();
		if(fopt.isPresent()) {
			ConfigurationNode file = fopt.get();
			for(String child : getChildren(file)) {
				ConfigurationNode arenaNode = file.getNode(child);
				Arena arena = new Arena(child,Main.access.game,Main.access);
				//Load all the saved value stored with the arena
				try {
					arena.getOptions().setMinPlayers(arenaNode.getNode("minPl").getInt());
					arena.getOptions().setMaxPlayers(arenaNode.getNode("maxPl").getInt());
					arena.getOptions().setLobbyCountdownTime(arenaNode.getNode("lobbyCtdnTime").getInt());
					arena.getOptions().setDedicatedServer(arenaNode.getNode("dedicated").getBoolean());
					arena.getOptions().setDefaultPlayerEventActions(arenaNode.getNode("dfltPlEvActions").getBoolean());
					arena.getOptions().setDefaultStateChangeActions(arenaNode.getNode("dfltStChActions").getBoolean());
					arena.getOptions().setEndGameDelay(arenaNode.getNode("endGameDelay").getBoolean());
					arena.getOptions().setEndGameSpectator(arenaNode.getNode("endGameSpec").getBoolean());
					arena.getOptions().setTriggerPlayerEvents(arenaNode.getNode("triggerPlEvents").getBoolean());
					if(arenaNode.getNode("lobbySpawn")!=null) {
						Optional<Location<World>> oLoc = loadLoc(arenaNode.getNode("lobbySpawn"));
						if(oLoc.isPresent()) {
							arena.setLobbySpawnLocation(oLoc.get());
						}
					}
					if(arenaNode.getNode("failedJoin")!=null) {
						Optional<Location<World>> oLoc = loadLoc(arenaNode.getNode("failedJoin"));
						if(oLoc.isPresent()) {
							arena.setFailedJoinLocation(oLoc.get());
						}
					}
					//Check for custom variables
					if(arenaNode.getNode("custom")!=null) {
						for(String customVar : getChildren(arenaNode.getNode("custom"))) {
							ConfigurationNode customNode = arenaNode.getNode("custom");
							Object rawVal = customNode.getNode(customVar).getValue();
							if(rawVal instanceof String) {
								arena.setVariable(customVar, (String) rawVal);
							} else if(rawVal instanceof Integer) {
								arena.setVariable(customVar, (int) rawVal);
							} else if(rawVal instanceof Boolean) {
								arena.setVariable(customVar, (boolean) rawVal);
							} else if(rawVal instanceof Double) {
								arena.setVariable(customVar, (double) rawVal);
							} else if(rawVal instanceof Float) {
								arena.setVariable(customVar, (float) rawVal);
							}
						}
					}
					//Check for custom locations
					if(arenaNode.getNode("customLoc")!=null) {
						for(String locName : getChildren(arenaNode.getNode("customLoc"))) {
							Optional<Location<World>> oLoc = loadLoc(arenaNode.getNode("customLoc").getNode(locName));
							if(oLoc.isPresent()) {
								arena.setLocation(locName, oLoc.get());
							}
						}
					}
					//Check for spawn locations
					if(arenaNode.getNode("spawns")!=null) {
						for(String locName : getChildren(arenaNode.getNode("spawns"))) {
							Optional<Location<World>> oLoc = loadLoc(arenaNode.getNode("spawns").getNode(locName));
							if(oLoc.isPresent()) {
								arena.addSpawnLocation(locName, oLoc.get());
							}
						}
					}
					arenas.add(arena);
				} catch(Exception e) {
					Main.access.logger.error("Corrupt arena data for arena "+child+"!");
				}
			}
		}
		return arenas;
	}
	
	private void saveLoc(ConfigurationNode node,Location<World> loc) {
		node.getNode("X").setValue(loc.getBlockX());
		node.getNode("Y").setValue(loc.getBlockY());
		node.getNode("Z").setValue(loc.getBlockZ());
		node.getNode("w").setValue(loc.getExtent().getName());
	}
	
	private Optional<Location<World>> loadLoc(ConfigurationNode node) {
		ConfigurationNode x = node.getNode("X");
		ConfigurationNode y = node.getNode("Y");
		ConfigurationNode z = node.getNode("Z");
		ConfigurationNode world = node.getNode("w");
		if(x==null||y==null||z==null||world==null||!Main.access.game.getServer().getWorld(world.getString()).isPresent()) {
			return Optional.empty();
		}
		try {
			return Optional.of(new Location<World>(Main.access.game.getServer().getWorld(world.getString()).get(),x.getInt(),y.getInt(),z.getInt()));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
	
	private ArrayList<String> getChildren(ConfigurationNode node) {
		ArrayList<String> children = new ArrayList<String>();
		for(Object raw : node.getChildrenMap().keySet()) {
			if(raw instanceof String) {
				children.add((String) raw);
			}
		}
		return children;
	}
}
