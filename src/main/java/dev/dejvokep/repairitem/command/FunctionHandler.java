package dev.dejvokep.repairitem.command;

import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A marking interface for command function handlers.
 */
public interface FunctionHandler extends Consumer<CommandContext<CommandSender>> {
}