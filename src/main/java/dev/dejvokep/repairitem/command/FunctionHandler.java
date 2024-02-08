package dev.dejvokep.repairitem.command;

import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;

public interface FunctionHandler extends BiConsumer<CommandContext<CommandSender>, CommandFunction> {
}