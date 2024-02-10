package dev.dejvokep.repairitem.command.handler;

import cloud.commandframework.context.CommandContext;
import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.function.FunctionHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Help function command handler.
 */
public class HelpCommand implements FunctionHandler {

    private final RepairItem plugin;

    /**
     * Initializes the command handler.
     *
     * @param plugin the plugin instance
     */
    public HelpCommand(@NotNull RepairItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(@NotNull CommandContext<CommandSender> context) {
        plugin.getMessenger().send(context, "help");
    }
}