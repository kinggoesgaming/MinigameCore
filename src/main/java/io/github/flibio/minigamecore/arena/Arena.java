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

import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import io.github.flibio.minigamecore.events.ArenaStateChangeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class Arena {

    private ArrayList<ArenaState> arenaStates = new ArrayList<>(getDefaultArenaStates());
    private HashMap<ArenaState, Runnable> runnables = new HashMap<>();

    private ArrayList<Player> onlinePlayers = new ArrayList<>();
    private ArenaState arenaState;

    private ArenaData arenaData;

    private Game game;
    private Object plugin;

    /**
     * Creates a new Arena.
     * 
     * @param arenaName The name of the arena.
     * @param game An instance of the game.
     * @param plugin An instance of the main class of the plugin.
     */
    public Arena(String arenaName, Game game, Object plugin) {
        this.arenaData = new ArenaData(arenaName);
        this.game = game;
        this.plugin = plugin;
        this.arenaState = ArenaStates.LOBBY_WAITING;

        game.getEventManager().registerListeners(plugin, this);
    }

    /**
     * Adds an online player.
     * 
     * @param player The player to add.
     */
    public abstract void addOnlinePlayer(Player player);

    /**
     * Removes an online player.
     * 
     * @param player The player to remove.
     */
    public abstract void removeOnlinePlayer(Player player);

    /**
     * Gets all of the players in an arena.
     * 
     * @return All the players in the arena.
     */
    public ArrayList<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    /**
     * Calls a state change on the arena.
     * 
     * @param newState The {@link ArenaState} to change the arena to.
     */
    public void arenaStateChange(ArenaState newState) {
        if (!arenaStates.contains(newState)) {
            return;
        }
        arenaState = newState;
        game.getEventManager().post(new ArenaStateChangeEvent(this, plugin));
        if (arenaStateRunnableExists(newState)) {
            runnables.get(newState).run();
        }
    }

    // Other Arena Properties

    /**
     * Gets the {@link ArenaData}.
     * 
     * @return The {@link ArenaData}.
     */
    public ArenaData getData() {
        return arenaData;
    }

    /**
     * Overrides the current {@link ArenaData}.
     * 
     * @param data The {@link ArenaData} that will override the current
     *        {@link ArenaData}.
     */
    public void overrideData(ArenaData data) {
        arenaData = data;
    }

    /**
     * Gets the current {@link ArenaState}.
     * 
     * @return The current {@link ArenaState}.
     */
    public ArenaState getArenaState() {
        return arenaState;
    }

    /**
     * Adds a new {@link ArenaState}.
     * 
     * @param state The {@link ArenaState} to add.
     * @return If the {@link ArenaState} was successfully added.
     */
    public boolean addArenaState(ArenaState state) {
        if (arenaStateExists(state)) {
            return false;
        } else {
            arenaStates.add(state);
            return true;
        }
    }

    /**
     * Removes an {@link ArenaState}.
     * 
     * @param state The {@link ArenaState} to remove.
     * @return If the {@link ArenaState} was successfully removed.
     */
    public boolean removeArenaState(ArenaState state) {
        if (getDefaultArenaStates().contains(state) || !arenaStateExists(state)) {
            return false;
        } else {
            if (runnables.keySet().contains(state)) {
                runnables.remove(state);
            }
            arenaStates.remove(state);
            return true;
        }
    }

    /**
     * Checks if an {@link ArenaState} exists.
     * 
     * @param arenaState The {@link ArenaState} to check for.
     * @return If the {@link ArenaState} exists.
     */
    public boolean arenaStateExists(ArenaState arenaState) {
        return arenaStates.contains(arenaState);
    }

    /**
     * Gets a list of the default {@link ArenaState}s.
     * 
     * @return A list of the default {@link ArenaState}s.
     */
    public List<ArenaState> getDefaultArenaStates() {
        return Arrays.asList(ArenaStates.LOBBY_WAITING, ArenaStates.LOBBY_COUNTDOWN, ArenaStates.GAME_COUNTDOWN,
                ArenaStates.GAME_PLAYING, ArenaStates.GAME_OVER);
    }

    /**
     * Adds an {@link ArenaState} runnable.
     * 
     * @param state The {@link ArenaState} to add.
     * @param runnable The runnable to add.
     * @return If the method was successful or not.
     */
    public boolean addArenaStateRunnable(ArenaState state, Runnable runnable) {
        if (!arenaStateExists(state) || arenaStateRunnableExists(state)) {
            return false;
        }
        runnables.put(state, runnable);
        return true;
    }

    /**
     * Removes an {@link ArenaState} runnable.
     * 
     * @param state The {@link ArenaState} to remove.
     * @return If the {@link ArenaState} was successfully removed.
     */
    public boolean removeArenaStateRunnable(ArenaState state) {
        if (!arenaStateExists(state) || !arenaStateRunnableExists(state)) {
            return false;
        }
        runnables.remove(state);
        return true;
    }

    /**
     * Checks if an {@link ArenaState} runnable exists.
     * 
     * @param state The {@link ArenaState} to check for.
     * @return If the {@link ArenaState} runnable exists.
     */
    public boolean arenaStateRunnableExists(ArenaState state) {
        return runnables.keySet().contains(state);
    }

    /**
     * Gets an {@link ArenaState} runnable.
     * 
     * @param state The {@link ArenaState} to get the runnable of.
     * @return The {@link ArenaState} runnable.
     */
    public Optional<Runnable> getArenaStateRunnable(ArenaState state) {
        if (arenaStateRunnableExists(state)) {
            return Optional.of(runnables.get(state));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Sends a message to each player.
     * 
     * @param text The text to send.
     */
    public void broadcast(Text text) {
        for (Player player : onlinePlayers) {
            player.sendMessage(text);
        }
    }

    /**
     * Plays a sound to all players in the game.
     * 
     * @param type The type of sound to play.
     * @param volume The volume of the sound.
     * @param pitch The pitch of the sound.
     */
    public void broadcastSound(SoundType type, int volume, int pitch) {
        for (Player player : onlinePlayers) {
            player.playSound(type, player.getLocation().getPosition(), volume, pitch);
        }
    }

    // Listeners

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
        if (arenaData.isTriggerPlayerEvents()) {
            Player player = event.getTargetEntity();
            removeOnlinePlayer(player);
            event.setChannel(MessageChannel.TO_NONE);
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (arenaData.isTriggerPlayerEvents()) {
            Player player = event.getTargetEntity();
            addOnlinePlayer(player);
            event.setChannel(MessageChannel.TO_NONE);
        }
    }

    @Listener
    public void onBlockModify(ChangeBlockEvent event, @First Player player) {
        if (!arenaData.isModifyLobbyBlocks()) {
            if (arenaState.equals(ArenaStates.LOBBY_COUNTDOWN) || arenaState.equals(ArenaStates.LOBBY_WAITING)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onPlayerDamage(DamageEntityEvent event, @First Player player) {
        if (!arenaData.isAllowLobbyDamage()) {
            if (arenaState.equals(ArenaStates.LOBBY_COUNTDOWN) || arenaState.equals(ArenaStates.LOBBY_WAITING)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onHungerChange(ChangeDataHolderEvent.ValueChange event) {
        if (!arenaData.isAllowHungerLoss()) {
            event.getEndResult().getReplacedData().forEach(iv -> {
                if (iv.getKey().equals(Keys.FOOD_LEVEL)) {
                    event.setCancelled(true);
                }
            });
        }
    }
}