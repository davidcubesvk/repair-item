package dev.dejvokep.repairitem.command;

import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Supported command functions.
 */
public enum CommandFunction {
    /**
     * Repairs all (inventory and armor) items.
     */
    ALL("Repairs all items."),
    /**
     * Repairs the inventory items (without the armor, including both hands).
     */
    INVENTORY("Repairs inventory items, without the armor."),
    /**
     * Repairs the armor.
     */
    ARMOR("Repairs the armor."),
    /**
     * Repairs the hotbar items (including both hands).
     */
    HOTBAR("Repairs hotbar items."),
    /**
     * Repairs both hands.
     */
    BOTH_HANDS("Repairs items in both hands."),
    /**
     * Repairs the main-hand.
     */
    MAIN_HAND("Repairs the main-hand."),
    /**
     * Repairs the off-hand.
     */
    OFF_HAND("Repairs the off-hand."),
    /**
     * Reloads the plugin.
     */
    RELOAD("Reloads the plugin."),
    /**
     * Shows the help page.
     */
    HELP("Displays the help page.");

    private final String path, permission, description;

    /**
     * Initializes the command function.
     * <p>
     * The function configuration path is constructed from the lowercase enum constant name, where dashes
     * (<code>-</code>) are replaced by underscores (<code>_</code>). The permission is also lowercase constant name,
     * but with the dashes (<code>-</code>) removed.
     *
     * @param handler the function handler
     * @param description description of the function
     */
    CommandFunction(FunctionHandler handler, String description) {
        this.path = name().toLowerCase().replace("_", "-");
        this.permission = name().toLowerCase().replace("_", "");
        this.description = description;
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