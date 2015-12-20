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
}
