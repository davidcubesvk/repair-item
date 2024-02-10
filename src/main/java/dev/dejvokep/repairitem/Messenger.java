package dev.dejvokep.repairitem;

import cloud.commandframework.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Messenger class used to send command messages.
 */
public class Messenger {

    /**
     * Message path prefix.
     */
    public static final String MESSAGE_PREFIX = "messages.";

    private final RepairItem plugin;

    /**
     * Initializes the messenger.
     *
     * @param plugin the plugin
     */
    public Messenger(@NotNull RepairItem plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends message to the sender of the given context.
     *
     * @param context   the command context
     * @param messageId ID of the message to send
     */
    public void send(@NotNull CommandContext<CommandSender> context, @NotNull String messageId) {
        send(context, messageId, null);
    }

    /**
     * Sends message to the given sender.
     *
     * @param sender    the sender to send to
     * @param messageId ID of the message to send
     */
    public void send(@NotNull CommandSender sender, @NotNull String messageId) {
        send(sender, messageId, null);
    }

    /**
     * Sends message to the sender of the given context. If provided, applies the given replacer to the message.
     *
     * @param context   the command context
     * @param messageId ID of the message to send
     * @param replacer  replacer to apply to the message
     */
    public void send(@NotNull CommandContext<CommandSender> context, @NotNull String messageId, @Nullable Function<String, String> replacer) {
        send(context.getSender(), messageId, replacer);
    }

    /**
     * Sends message to the given sender. If provided, applies the given replacer to the message.
     *
     * @param sender    the sender to send to
     * @param messageId ID of the message to send
     * @param replacer  replacer to apply to the message
     */
    public void send(@NotNull CommandSender sender, @NotNull String messageId, @Nullable Function<String, String> replacer) {
        // Not online
        if (sender instanceof Player && !((Player) sender).isOnline())
            return;

        // Validate
        String message = plugin.getConfiguration().getString(messageId);
        if (message == null || message.isEmpty())
            return;

        // Send
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', replacer == null ? message : replacer.apply(message)));
    }

}