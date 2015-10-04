package me.flibio.minigamecore.file;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager {
	
	private ConcurrentHashMap<String, ConfigurationNode> files = new ConcurrentHashMap<String, ConfigurationNode>();
	
	private Logger logger;
	private String name;
	
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
	public boolean generateFolder(String name) {
		File folder = new File(name);
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
	 * Initializes a file into the file manager
	 * @param fileName
	 * 	The name of the file to initialize
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean initializeFile(String fileName) {
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
	 * Gets an initialized file
	 * @param name
	 * 	The name of the file to get
	 * @return
	 * 	The ConfigurationNode of the file
	 */
	public Optional<ConfigurationNode> getFile(String name) {
		if(files.containsKey(name)) {
			return Optional.of(files.get(name));
		} else {
			return Optional.empty();
		}
	}
}
