package dev.dejvokep.repairitem.command.handler;

import cloud.commandframework.context.CommandContext;
import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.CommandFunction;
import dev.dejvokep.repairitem.command.FunctionHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;

public class HelpCommand implements FunctionHandler {

    private final RepairItem plugin;

    public HelpCommand(@NotNull RepairItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(@NotNull CommandContext<CommandSender> context) {
        plugin.getMessenger().send(context, "help");
    }
}