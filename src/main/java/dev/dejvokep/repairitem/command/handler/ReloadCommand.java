package dev.dejvokep.repairitem.command.handler;

import cloud.commandframework.context.CommandContext;
import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.CommandFunction;
import dev.dejvokep.repairitem.command.FunctionHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

public class ReloadCommand implements FunctionHandler {

    private final RepairItem plugin;

    public ReloadCommand(@NotNull RepairItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(@NotNull CommandContext<CommandSender> context, @NotNull CommandFunction function) {
        try {
            plugin.getConfiguration().reload();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst reloading plugin configuration!", ex);
        }
        plugin.getRepairer().reload();
        plugin.getMessenger().send(context, "reload");
    }
}