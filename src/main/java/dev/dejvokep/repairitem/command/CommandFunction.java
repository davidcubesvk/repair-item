package dev.dejvokep.repairitem.command;

import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.handler.HelpCommand;
import dev.dejvokep.repairitem.command.handler.ReloadCommand;
import dev.dejvokep.repairitem.command.handler.RepairCommand;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Supported command functions.
 */
public enum CommandFunction {
    /**
     * Repairs all (inventory and armor) items.
     */
    ALL("Repairs all items.", RepairCommand::new),
    /**
     * Repairs the inventory items (without the armor, including both hands).
     */
    INVENTORY("Repairs inventory items, without the armor.", RepairCommand::new),
    /**
     * Repairs the armor.
     */
    ARMOR("Repairs the armor.", RepairCommand::new),
    /**
     * Repairs the hotbar items (including both hands).
     */
    HOTBAR("Repairs hotbar items.", RepairCommand::new),
    /**
     * Repairs both hands.
     */
    BOTH_HANDS("Repairs items in both hands.", RepairCommand::new),
    /**
     * Repairs the main-hand.
     */
    MAIN_HAND("Repairs the main-hand.", RepairCommand::new),
    /**
     * Repairs the off-hand.
     */
    OFF_HAND("Repairs the off-hand.", RepairCommand::new),
    /**
     * Reloads the plugin.
     */
    RELOAD("Reloads the plugin.", (plugin, function) -> new ReloadCommand(plugin)),
    /**
     * Shows the help page.
     */
    HELP("Displays the help page.", (plugin, function) -> new HelpCommand(plugin));

    private final String path, permission, description;
    private final BiFunction<RepairItem, CommandFunction, FunctionHandler> handlerInitializer;

    /**
     * Initializes the command function.
     * <p>
     * The function configuration path is constructed from the lowercase enum constant name, where dashes
     * (<code>-</code>) are replaced by underscores (<code>_</code>). The permission is also lowercase constant name,
     * but with the dashes (<code>-</code>) removed.
     *
     * @param description        description of the function
     * @param handlerInitializer function handler initializer
     */
    CommandFunction(String description, BiFunction<RepairItem, CommandFunction, FunctionHandler> handlerInitializer) {
        this.path = name().toLowerCase().replace("_", "-");
        this.permission = name().toLowerCase().replace("_", "");
        this.description = description;
        this.handlerInitializer = handlerInitializer;
    }

    /**
     * Initializes a handler for this function.
     * @param plugin the plugin instance
     * @return the created handler
     */
    public FunctionHandler initHandler(RepairItem plugin) {
        return handlerInitializer.apply(plugin, this);
    }

    /**
     * Returns the configuration path of the function.
     *
     * @return the configuration path of the function
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the permission suffix of the function (<code>repairitem.&lt;permission&gt;.self|other</code>).
     *
     * @return the permission suffix of the function
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Returns the description of the function.
     *
     * @return the description of the function
     */
    public String getDescription() {
        return description;
    }
}