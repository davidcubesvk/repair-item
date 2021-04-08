package com.davidcubesvk.repairItem.command;

import com.davidcubesvk.repairItem.RepairItem;
import com.davidcubesvk.repairItem.utils.RepairResult;
import com.davidcubesvk.repairItem.utils.Repairer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Command executor for the main plugin command <code>/repair</code>.
 */
public class Command implements CommandExecutor {

    /**
     * All command functions.
     * <ul>
     *     <li><code>ALL</code>: repairs everything - e.g. inventory and armor</li>
     *     <li><code>INVENTORY</code>: repairs the inventory (without the armor) including both hands</li>
     *     <li><code>ARMOR</code>: repairs the armor</li>
     *     <li><code>HOT_BAR</code>: repairs the hot-bar including both hands</li>
     *     <li><code>BOTH_HANDS</code>: repairs both hands (check if the off hand is supported)</li>
     *     <li><code>MAIN_HAND</code>: repairs the main hand</li>
     *     <li><code>OFF_HAND</code>: repairs the off hand (check if supported)</li>
     *     <li><code>RELOAD</code>: reloads the plugin</li>
     *     <li><code>HELP</code>: shows the help page</li>
     * </ul>
     */
    public enum Function {
        ALL("all"), INVENTORY("inventory"), ARMOR("armor"),
        HOT_BAR("hotBar"), BOTH_HANDS("bothHands"), MAIN_HAND("mainHand"),
        OFF_HAND("offHand"), RELOAD("reload"), HELP("help");

        //The path
        private String path;
        //The permission suffix
        private String permissionSuffix;

        /**
         * Initializes the path and permission.
         *
         * @param permissionSuffix the permission suffix for the function
         */
        Function(String permissionSuffix) {
            this.path = name().toLowerCase().replace("_", "-");
            this.permissionSuffix = permissionSuffix;
        }

        /**
         * Returns the configuration file message path of the function.
         *
         * @return the configuration file message path of the function
         */
        public String getPath() {
            return path;
        }

        /**
         * Returns the permission suffix of the function.
         *
         * @return the permission suffix of the function
         */
        public String getPermissionSuffix() {
            return permissionSuffix;
        }
    }

    /**
     * The base permission node.
     */
    public static final String PERMISSION_BASE = "repairItem";

    /**
     * The path of the main message section.
     */
    public static final String PATH_MAIN = "message";

    /**
     * The path of the sender message section.
     */
    public static final String PATH_REPAIR_SENDER = PATH_MAIN + ".repair.sender";

    /**
     * The path of the sender-success message section.
     */
    public static final String PATH_REPAIR_SENDER_SUCCESS = PATH_REPAIR_SENDER + ".success";

    /**
     * The path of the sender-fail message section.
     */
    public static final String PATH_REPAIR_SENDER_FAIL = PATH_REPAIR_SENDER + ".fail";

    /**
     * The path of the sender-target-placeholder message section.
     */
    public static final String PATH_REPAIR_SENDER_TARGET_PLACEHOLDER = PATH_REPAIR_SENDER + ".target-placeholder";

    /**
     * The path of the target message section.
     */
    public static final String PATH_REPAIR_TARGET = PATH_MAIN + ".repair.target";

    //The command functions
    private final Map<String, Function> functions = new HashMap<>();
    //Target parameters describing all-player target
    private Collection<String> allTarget;

    //The main class
    private RepairItem plugin;

    /**
     * Initializes the command with the given main class.
     *
     * @param plugin the main plugin class
     */
    public Command(RepairItem plugin) {
        //Set the plugin
        this.plugin = plugin;
        //Register the command
        Bukkit.getPluginCommand("repair").setExecutor(this);
    }

    /**
     * Reloads the function bindings.
     */
    public void reload() {
        //Clear the map
        functions.clear();

        //The function configuration section
        ConfigurationSection section = plugin.getConfiguration().getConfigurationSection("command.function");
        //Function (reused)
        Function function;
        //Loop through all keys
        for (String key : section.getKeys(false)) {
            //The content
            function = Function.valueOf(key.toUpperCase().replace("-", "_"));

            //Loop through all assignments
            for (String assignment : section.getStringList(key))
                functions.put(assignment, function);
        }

        //Add the arguments for all players target
        allTarget = plugin.getConfiguration().getStringList("command.target.all");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        //The configuration file
        Configuration config = plugin.getConfiguration();

        //If insufficient argument array length
        if (args.length < 1 || args.length > 2) {
            //Invalid format
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(PATH_MAIN + ".invalid-format")));
            return true;
        }

        //The function
        Function function = functions.get(args[0]);

        //If not found
        if (function == null) {
            //Invalid format
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(PATH_MAIN + ".invalid-format")));
            return true;
        }

        //If reload or help
        if (function == Function.RELOAD || function == Function.HELP) {
            //If does not have the permission
            if (!hasPermission(sender, PERMISSION_BASE + "." + function.getPermissionSuffix()))
                return true;

            //If invalid array length
            if (args.length != 1) {
                //Invalid format
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(PATH_MAIN + ".invalid-format")));
                return true;
            }

            //If reload
            if (function == Function.RELOAD) {
                //Config
                plugin.load();
                //Repairer
                plugin.getRepairer().reload();
                //Command
                this.reload();

                //Send the message
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(PATH_MAIN + ".reload")));
            } else {
                //Send the help page
                for (String line : config.getStringList(PATH_MAIN + ".help"))
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
            return true;
        }

        //If does not have the permission
        if (!hasPermission(sender, PERMISSION_BASE + "." + (args.length == 2 ? "target." : "") + function.getPermissionSuffix()))
            return true;

        //The repairer instance
        Repairer repairer = plugin.getRepairer();
        //The player sender
        Player playerSender = sender instanceof Player ? (Player) sender : null;

        //If no target argument was specified
        if (args.length == 1) {
            //If not called by a player
            if (playerSender == null) {
                //Player only
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(PATH_REPAIR_SENDER + ".player-only")));
                return true;
            }

            //Repair and send the message
            sendMessage(sender, config, PATH_REPAIR_SENDER, function, repairer.repair(playerSender, function), null, config.getString(PATH_REPAIR_SENDER_TARGET_PLACEHOLDER + ".self"));
        } else {
            //The target placeholder
            String targetPlaceholder = "";

            //Players to repair the items for
            List<Player> toRepair = new ArrayList<>();
            //If all players
            if (allTarget.contains(args[1])) {
                //Add all players
                toRepair.addAll(Bukkit.getOnlinePlayers());
                //Set the placeholder
                targetPlaceholder = config.getString(PATH_REPAIR_SENDER_TARGET_PLACEHOLDER + ".all");

                //If only one player
                if (toRepair.size() == 1)
                    //Set the target to the name
                    targetPlaceholder = toRepair.get(0).getName();

                //If only one player and it is the sender
                if (toRepair.size() == 1 && playerSender != null && toRepair.get(0).getUniqueId().equals(playerSender.getUniqueId())) {
                    //Repair and send the message
                    sendMessage(sender, config, PATH_REPAIR_SENDER, function, repairer.repair(playerSender.getPlayer(), function), null, config.getString(PATH_REPAIR_SENDER_TARGET_PLACEHOLDER + ".self"));
                    return true;
                }
            } else {
                //Get the target player
                Player player = Bukkit.getPlayerExact(args[1]);
                //If the target player is online
                if (player != null) {
                    //Add the player
                    toRepair.add(player);
                    //If sent by the player same as sender
                    if (playerSender != null && player.getUniqueId().equals(playerSender.getUniqueId())) {
                        //Repair and send the message
                        sendMessage(sender, config, PATH_REPAIR_SENDER, function, repairer.repair(playerSender.getPlayer(), function), null, config.getString(PATH_REPAIR_SENDER_TARGET_PLACEHOLDER + ".self"));
                        return true;
                    } else {
                        //Set the placeholder
                        targetPlaceholder = player.getName();
                    }
                }
            }

            //If no players are in the collection (no one is online/the target player is not online)
            if (toRepair.size() == 0) {
                //No players found
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(PATH_REPAIR_SENDER + ".player-not-found")));
                return true;
            }

            //The sender's name
            String senderName = sender.getName();

            //The amount of repaired items
            int repaired = 0;
            //The repair result (reused) also used as last result (for one-target repair)
            RepairResult result = null;
            //Go through all players
            for (Player player : toRepair) {
                //Result
                result = repairer.repair(player, function);
                //Add repaired
                repaired += result.getRepaired();

                //Send the player a message
                sendMessage(player, config, PATH_REPAIR_TARGET, function, result, senderName, null);
            }

            //If succeeded
            if (repaired > 0) {
                //Send the sender a message
                sendMessage(sender, config, PATH_REPAIR_SENDER, function, RepairResult.Status.SUCCESS, repaired, null, targetPlaceholder);
                return true;
            }

            //If more than 1 player was repaired
            if (toRepair.size() > 1)
                //Send the generalized message
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(PATH_REPAIR_SENDER_FAIL + ".generalized")
                        .replace("{target}", targetPlaceholder).replace("{repaired}", "" + repaired)));
            else
                //Send the detailed message
                sendMessage(sender, config, PATH_REPAIR_SENDER, function, result, null, targetPlaceholder);
        }

        return true;
    }

    /**
     * Returns if the sender has the specified permission (or shortened parent permissions ending with <code>.*</code>).
     * If not, sends the standard no permission message.
     *
     * @return if the sender has any (specified or parent) permission
     */
    public boolean hasPermission(CommandSender sender, String permission) {
        //If has the permission
        boolean hasPermission = false;

        //Check all existing permissions (a.b.c > a.b.* > a.*)
        while (permission.contains(".")) {
            //Check the permission
            hasPermission = sender.hasPermission(permission);

            //If has, break
            if (hasPermission) break;

            //Shorten permission check another
            permission = permission.substring(0, permission.lastIndexOf(".")) + ".*";
        }

        //Send message if does not have any permission
        if (!hasPermission)
            //Send no permission message
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfiguration().getString(PATH_MAIN + ".no-permission")));

        //Return if has permission
        return hasPermission;
    }

    /**
     * Sends a repair message to a recipient.
     *
     * @param recipient the message recipient
     * @param config    the configuration file
     * @param basePath  the base path (path to the configuration section where message for each function is located)
     * @param function  the command (repair) function called
     * @param result    the repair result
     * @param sender    the sender's name (if sending to the target), otherwise <code>null</code>
     * @param target    the target parameter (if used and sending message to the sender), otherwise <code>null</code>
     */
    private void sendMessage(CommandSender recipient, Configuration config, String basePath, Function function, RepairResult result, String sender, String target) {
        sendMessage(recipient, config, basePath, function, result.getStatus(), result.getRepaired(), sender, target);
    }

    /**
     * Sends a repair message to a recipient.
     *
     * @param recipient the message recipient
     * @param config    the configuration file
     * @param basePath  the base path (path to the configuration section for sender/target messages)
     * @param function  the command (repair) function called
     * @param status    the repair status
     * @param repaired  amount of items repaired
     * @param sender    the sender's name (if sending to the target), otherwise <code>null</code>
     * @param target    the target parameter (if used and sending message to the sender), otherwise <code>null</code>
     */
    private void sendMessage(CommandSender recipient, Configuration config, String basePath, Function function, RepairResult.Status status, int repaired, String sender, String target) {
        //The message
        String message = ChatColor.translateAlternateColorCodes('&',
                config.getString(basePath + "." + (status == RepairResult.Status.SUCCESS ? "success." + function.getPath() : "fail." + status.getPath())));
        //If sender specified
        if (sender != null)
            //Replace
            message = message.replace("{sender}", sender);
        //If target specified
        if (target != null)
            //Replace
            message = message.replace("{target}", target);
        //Send the message
        recipient.sendMessage(message.replace("{repaired}", "" + repaired));
    }
}