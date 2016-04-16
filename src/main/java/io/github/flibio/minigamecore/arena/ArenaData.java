/*
 * This file is part of MinigameCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 - 2016 Flibio
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.flibio.minigamecore.arena;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

public class ArenaData implements Serializable {

    private static final long serialVersionUID = 1L;
    private HashMap<String, Object> customVariables = new HashMap<>();
    private HashMap<String, Location<World>> locations = new HashMap<>();

    private final String name;
    private boolean triggerPlayerEvents = true;
    private boolean modifyLobbyBlocks = false;
    private boolean allowLobbyDamage = false;
    private boolean allowHungerLoss = false;

    /**
     * Stores data for the arena.
     * 
     * @param arenaName The name of the arena.
     */
    public ArenaData(String arenaName) {
        this.name = arenaName;
    }

    /**
     * Gets the name of the arena.
     * 
     * @return The name of the arena.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if MinigameCore will automatically trigger player join and player
     * quit methods when a player joins or quits the server.
     * 
     * @return If MinigameCore will automatically trigger player join and player
     *         quit methods when a player joins or quits the server.
     */
    public boolean isTriggerPlayerEvents() {
        return triggerPlayerEvents;
    }

    /**
     * Sets if MinigameCore will automatically trigger player join and player
     * quit methods when a player joins or quits the server.
     * 
     * @param triggerPlayerEvents If MinigameCore will automatically trigger
     *        player join and player quit methods when a player joins or quits
     *        the server.
     */
    public void setTriggerPlayerEvents(boolean triggerPlayerEvents) {
        this.triggerPlayerEvents = triggerPlayerEvents;
    }

    /**
     * Checks if MinigameCore will block players from modifying blocks in the
     * lobby.
     * 
     * @return If MinigameCore will block players from modifying blocks in the
     *         lobby.
     */
    public boolean isModifyLobbyBlocks() {
        return modifyLobbyBlocks;
    }

    /**
     * Sets if MinigameCore will block players from modifying blocks in the
     * lobby.
     * 
     * @param defaultPlayerEventActions If MinigameCore will block players from
     *        modifying blocks in the lobby.
     */
    public void setModifyLobbyBlocks(boolean modifyLobbyBlocks) {
        this.modifyLobbyBlocks = modifyLobbyBlocks;
    }

    /**
     * Checks if MinigameCore will allow players to take damage in the lobby.
     * 
     * @return If MinigameCore will allow players to take damage in the lobby.
     */
    public boolean isAllowLobbyDamage() {
        return allowLobbyDamage;
    }

    /**
     * Sets if MinigameCore will allow players to take damage in the lobby.
     * 
     * @param allowLobbyDamage If MinigameCore will allow players to take damage
     *        in the lobby.
     */
    public void setAllowLobbyDamage(boolean allowLobbyDamage) {
        this.allowLobbyDamage = allowLobbyDamage;
    }

    /**
     * Checks if MinigameCore will allow players to lose hunger.
     * 
     * @return If MinigameCore will allow players to lose hunger.
     */
    public boolean isAllowHungerLoss() {
        return allowHungerLoss;
    }

    /**
     * Sets if MinigameCore will allow players to lose hunger.
     * 
     * @param allowLobbyDamage If MinigameCore will allow players to lose
     *        hunger.
     */
    public void setAllowHungerLoss(boolean allowHungerLoss) {
        this.allowHungerLoss = allowHungerLoss;
    }

    // Custom Variables

    /**
     * Set the value of a key. The value must be serializable.
     * 
     * @param key The key to store with the value.
     * @param type The type of the value.
     * @param value The value to store with the key.
     * @return If the value was successfully set.
     */
    public <T> boolean setVariable(String key, Class<T> type, T value) {
        if (Serializable.class.isAssignableFrom(type)) {
            customVariables.put(key, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets a variable using a key.
     * 
     * @param key The key of the variable.
     * @param type The type of the variable stored.
     * @return The variable, if it was found.
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getVariable(String key, Class<T> type) {
        if (customVariables.containsKey(key)) {
            Object raw = customVariables.get(key);
            if (type.equals(raw.getClass())) {
                return Optional.of((T) raw);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all of the custom variables stored with the arena.
     * 
     * @return A HashMap of all of the custom variables.
     */
    public HashMap<String, Object> getCustomVariables() {
        final HashMap<String, Object> toReturn = this.customVariables;
        return toReturn;
    }

    /**
     * Assigns a location to a key.
     * 
     * @param key The key the location will be assigned to.
     * @param location The location to assign.
     */
    public void setLocation(String key, Location<World> location) {
        locations.put(key, location);
    }

    /**
     * Gets a location using the specified key.
     * 
     * @param key The key of the location to get.
     * @return The location, if found.
     */
    public Optional<Location<World>> getLocation(String key) {
        if (locations.containsKey(key)) {
            return Optional.of(locations.get(key));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets all of the locations stored with the arena.
     * 
     * @return A HashMap of all of the locations.
     */
    public HashMap<String, Location<World>> getLocations() {
        final HashMap<String, Location<World>> toReturn = this.locations;
        return toReturn;
    }

}
