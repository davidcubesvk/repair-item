package dev.dejvokep.repairitem.command.handler;

import cloud.commandframework.context.CommandContext;
import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.CommandFunction;
import dev.dejvokep.repairitem.command.FunctionHandler;
import dev.dejvokep.repairitem.command.Sender;
import dev.dejvokep.repairitem.command.Target;
import dev.dejvokep.repairitem.repair.RepairResult;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;

public class RepairCommand implements FunctionHandler {

    private final RepairItem plugin;

    public RepairCommand(@NotNull RepairItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(@NotNull CommandContext<CommandSender> context, @NotNull CommandFunction function) {
        String targetName = context.getOrDefault("target", null);

        if (targetName == null) {
            if (!(context.getSender() instanceof Player)) {
                plugin.getMessenger().send(context, "repair.sender.error.players-only");
                return;
            }

            RepairResult result = plugin.getRepairer().repair((Player) context.getSender(), function);
            message(function, Sender.of(context.getSender()), Target.of((Player) context.getSender()), result);
            return;
        }

        if (plugin.getCommand().getAllTarget().contains(targetName)) {
            Target target = Target.online();
            message(function, Sender.of(context.getSender()), target, );
        }

        plugin.getMessenger().send(context, "reload");
    }

    private void message(CommandFunction function, Sender sender, Target target, RepairResult result) {
        String targetReplacement = target.getReplacement(sender.get(), plugin.getConfiguration());
        plugin.getMessenger().send(sender.get(), "repair.sender." + result.getStatus().getPath(function), message -> message
                .replace("{target}", targetReplacement)
                .replace("{repaired}", String.valueOf(result.getRepaired())));

        if (target.is(sender.get()))
            return;

        String senderReplacement = sender.getReplacement(plugin.getConfiguration());
        for (Player player : target.getPlayers()) {
            plugin.getMessenger().send(player, "repair.target." + result.getStatus().getPath(function), message -> message
                    .replace("{sender}", senderReplacement)
                    .replace("{repaired}", String.valueOf(result.getRepaired())));
        }
    }
}