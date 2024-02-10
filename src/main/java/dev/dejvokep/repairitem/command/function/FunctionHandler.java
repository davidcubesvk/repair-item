package dev.dejvokep.repairitem.command.function;

import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A marker interface for command function handlers.
 */
public interface FunctionHandler extends Consumer<CommandContext<CommandSender>> {
}