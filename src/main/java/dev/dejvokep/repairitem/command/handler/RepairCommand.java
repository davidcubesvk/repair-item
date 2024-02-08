package dev.dejvokep.repairitem.command.handler;

import cloud.commandframework.context.CommandContext;
import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.CommandFunction;
import dev.dejvokep.repairitem.command.FunctionHandler;
import dev.dejvokep.repairitem.command.Sender;
import dev.dejvokep.repairitem.command.Target;
import dev.dejvokep.repairitem.repair.RepairResult;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RepairCommand implements FunctionHandler {

    private final RepairItem plugin;

    public RepairCommand(@NotNull RepairItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(@NotNull CommandContext<CommandSender> context, @NotNull CommandFunction function) {
        Sender sender = Sender.of(context.getSender());
        String targetName = context.getOrDefault("target", null);

        if (targetName == null) {
            if (!(context.getSender() instanceof Player)) {
                plugin.getMessenger().send(context, "repair.sender.error.players-only");
                return;
            }

            run(function, sender, Target.of((Player) context.getSender()));
            return;
        }

        if (plugin.getCommand().getAllTarget().contains(targetName)) {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                plugin.getMessenger().send(context, "repair.sender.error.player-offline");
                return;
            }

            run(function, sender, Target.online());
        }

        Player player = Bukkit.getPlayerExact(targetName);
        if (player == null) {
            plugin.getMessenger().send(context, "repair.sender.error.player-offline");
            return;
        }

        run(function, sender, Target.of(player));
    }

    private void run(CommandFunction function, Sender sender, Target target) {
        String targetReplacement = target.getReplacement(sender.get(), plugin.getConfiguration());
        String senderReplacement = sender.getReplacement(plugin.getConfiguration());

        if (target.getPlayers().size() == 1) {
            Player player = target.getOne();
            RepairResult result = plugin.getRepairer().repair(player, function);

            plugin.getMessenger().send(sender.get(), "repair.sender." + result.getStatus().getPath(function), message -> message
                    .replace("{target}", targetReplacement)
                    .replace("{repaired}", String.valueOf(result.getRepaired())));

            if (player == sender.get())
                return;

            plugin.getMessenger().send(player, "repair.target." + result.getStatus().getPath(function), message -> message
                    .replace("{sender}", senderReplacement)
                    .replace("{repaired}", String.valueOf(result.getRepaired())));
            return;
        }

        int repaired = 0;
        for (Player player : target.getPlayers()) {
            RepairResult result = plugin.getRepairer().repair(player, function);
            repaired += result.getRepaired();

            plugin.getMessenger().send(player, "repair.target." + result.getStatus().getPath(function), message -> message
                    .replace("{sender}", senderReplacement)
                    .replace("{repaired}", String.valueOf(result.getRepaired())));
        }

        final int repaired0 = repaired;
        plugin.getMessenger().send(sender.get(), "repair.sender." + RepairResult.Status.SUCCESS.getPath(function), message -> message
                .replace("{target}", targetReplacement)
                .replace("{repaired}", String.valueOf(repaired0)));
    }
}