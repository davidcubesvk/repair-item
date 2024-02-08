package dev.dejvokep.repairitem.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.route.Route;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class Target {

    private static final Route ROUTE_REPLACEMENT_SELF = Route.fromString("messages.repair.sender.target-placeholder.self");
    private static final Route ROUTE_REPLACEMENT_ALL = Route.fromString("messages.repair.sender.target-placeholder.all");

    private final Collection<? extends Player> players;

    private Target(Collection<? extends Player> players) {
        if (players.isEmpty())
            throw new IllegalArgumentException("Target player collection cannot be empty!");

        this.players = players;
    }

    public String getReplacement(CommandSender sender, YamlDocument config) {
        if (players.size() > 1)
            return config.getString(ROUTE_REPLACEMENT_ALL);

        Player one = players.iterator().next();
        if (one == sender)
            return config.getString(ROUTE_REPLACEMENT_SELF);

        return one.getName();
    }

    public boolean is(CommandSender sender) {
        return players.size() == 1 && players.iterator().next() == sender;
    }

    public Collection<? extends Player> getPlayers() {
        return players;
    }

    public static Target online() {
        return new Target(Bukkit.getOnlinePlayers());
    }

    public static Target of(Player player) {
        return new Target(Collections.singleton(player));
    }

}