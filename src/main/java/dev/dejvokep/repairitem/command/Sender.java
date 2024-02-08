package dev.dejvokep.repairitem.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Sender {

    private static final Route ROUTE_REPLACEMENT_CONSOLE = Route.fromString("messages.repair.target.source-placeholder.console");

    private final CommandSender sender;

    private Sender(CommandSender sender) {
        this.sender = sender;
    }

    public String getReplacement(YamlDocument config) {
        return sender instanceof ConsoleCommandSender ? config.getString(ROUTE_REPLACEMENT_CONSOLE) : sender.getName();
    }

    public CommandSender get() {
        return sender;
    }

    public static Sender of(CommandSender sender) {
        return new Sender(sender);
    }

}