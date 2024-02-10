/*
 * Copyright 2024 https://dejvokep.dev/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.dejvokep.repairitem.command.wrapper;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.route.Route;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * A representation for a collection of target players.
 */
public class Target {

    /**
     * Route to the replacement for the <code>{target}</code> placeholder when issuing a self repair.
     */
    public static final Route ROUTE_REPLACEMENT_SELF = Route.fromString("messages.repair.sender.target-placeholder.self");
    /**
     * Route to the replacement for the <code>{target}</code> placeholder when issuing a repair for all players.
     */
    public static final Route ROUTE_REPLACEMENT_ALL = Route.fromString("messages.repair.sender.target-placeholder.all");

    private final Collection<? extends Player> players;

    /**
     * Constructs a target from the given <b>non-empty</b> collection of players.
     *
     * @param players the target players
     */
    private Target(@NotNull Collection<? extends Player> players) {
        if (players.isEmpty())
            throw new IllegalArgumentException("Target player collection cannot be empty!");

        this.players = players;
    }

    /**
     * Returns the replacement for the <code>{target}</code> placeholder.
     *
     * @param sender the sender of the command
     * @param config the config
     * @return the placeholder replacement
     */
    public String getReplacement(@NotNull CommandSender sender, @NotNull YamlDocument config) {
        if (players.size() > 1)
            return config.getString(ROUTE_REPLACEMENT_ALL);

        Player one = getOne();
        if (one == sender)
            return config.getString(ROUTE_REPLACEMENT_SELF);

        return one.getName();
    }

    /**
     * Returns one player from the target player {@link Collection collection} (the first player returned by the
     * {@link Collection#iterator() collection's iterator}).
     *
     * @return
     */
    @NotNull
    public Player getOne() {
        return players.iterator().next();
    }

    /**
     * Returns if the target is the given sender. More specifically, returns <code>true</code> only if this target
     * contains only one player - the given sender.
     *
     * @param sender the sender to compare with
     * @return if this target represents the given sender
     */
    public boolean is(@NotNull CommandSender sender) {
        return players.size() == 1 && players.iterator().next() == sender;
    }

    /**
     * Returns the target player collection.
     *
     * @return the target player collection
     */
    @NotNull
    public Collection<? extends Player> getPlayers() {
        return players;
    }

    /**
     * Constructs a target from all {@link Bukkit#getOnlinePlayers() online players}. There must be at least one online
     * player, otherwise, an {@link IllegalArgumentException} will be thrown.
     *
     * @return the target
     */
    @NotNull
    public static Target online() {
        return new Target(Bukkit.getOnlinePlayers());
    }

    /**
     * Constructs a singleton target from the given player.
     *
     * @param player the player target
     * @return the target
     */
    @NotNull
    public static Target of(Player player) {
        return new Target(Collections.singleton(player));
    }

}