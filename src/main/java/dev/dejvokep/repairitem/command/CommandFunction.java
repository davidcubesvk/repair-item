package dev.dejvokep.repairitem.command;

/**
 * Supported command functions.
 */
public enum CommandFunction {
    /**
     * Repairs all (inventory and armor) items.
     */
    ALL("all"),
    /**
     * Repairs the inventory items (without the armor, including both hands).
     */
    INVENTORY("inventory"),
    /**
     * Repairs the armor.
     */
    ARMOR("armor"),
    /**
     * Repairs the hotbar items (including both hands).
     */
    HOTBAR("hotBar"),
    /**
     * Repairs both hands.
     */
    BOTH_HANDS("bothHands"),
    /**
     * Repairs the main-hand.
     */
    MAIN_HAND("mainHand"),
    /**
     * Repairs the off-hand.
     */
    OFF_HAND("offHand"),
    /**
     * Reloads the plugin.
     */
    RELOAD("reload"),
    /**
     * Shows the help page.
     */
    HELP("help");

    private final String path;
    private final String permissionSuffix;

    /**
     * Initializes the command function.
     * <p>
     * The function configuration path is constructed from the lowercase enum constant name, where dashes
     * (<code>-</code>) are replaced by underscores (<code>_</code>).
     *
     * @param permissionSuffix the permission suffix for the function
     */
    CommandFunction(String permissionSuffix) {
        this.path = name().toLowerCase().replace("_", "-");
        this.permissionSuffix = permissionSuffix;
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
     * Returns the permission suffix of the function.
     *
     * @return the permission suffix of the function
     */
    public String getPermissionSuffix() {
        return permissionSuffix;
    }
}