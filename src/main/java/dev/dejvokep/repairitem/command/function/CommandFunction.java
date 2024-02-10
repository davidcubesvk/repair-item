/*
 * Copyright 2024 https://dejvokep.dev/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.dejvokep.repairitem.command.function;

import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.handler.HelpCommand;
import dev.dejvokep.repairitem.command.handler.ReloadCommand;
import dev.dejvokep.repairitem.command.handler.RepairCommand;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

/**
 * Supported command functions.
 */
public enum CommandFunction {

    /**
     * Repairs all (inventory and armor) items.
     */
    ALL("Repairs all items.", RepairCommand::new, true),
    /**
     * Repairs the inventory items (without the armor, including both hands).
     */
    INVENTORY("Repairs inventory items, without the armor.", RepairCommand::new, true),
    /**
     * Repairs the armor.
     */
    ARMOR("Repairs the armor.", RepairCommand::new, true),
    /**
     * Repairs the hotbar items (including both hands).
     */
    HOTBAR("Repairs hotbar items.", RepairCommand::new, true),
    /**
     * Repairs both hands.
     */
    BOTH_HANDS("Repairs items in both hands.", RepairCommand::new, true),
    /**
     * Repairs the main-hand.
     */
    MAIN_HAND("Repairs the main-hand.", RepairCommand::new, true),
    /**
     * Repairs the off-hand.
     */
    OFF_HAND("Repairs the off-hand.", RepairCommand::new, true),
    /**
     * Reloads the plugin.
     */
    RELOAD("Reloads the plugin.", (plugin, function) -> new ReloadCommand(plugin), false),
    /**
     * Shows the help page.
     */
    HELP("Displays the help page.", (plugin, function) -> new HelpCommand(plugin), false);

    private final String path, permission, description;
    private final BiFunction<RepairItem, CommandFunction, FunctionHandler> handlerInitializer;
    private final boolean hasTarget;

    /**
     * Initializes the command function.
     * <p>
     * The function configuration path is constructed from the lowercase enum constant name, where dashes
     * (<code>-</code>) are replaced by underscores (<code>_</code>). The permission is also lowercase constant name,
     * but with the dashes (<code>-</code>) removed.
     *
     * @param description        description of the function
     * @param handlerInitializer function handler initializer
     * @param hasTarget          if this function also has a command variant with the <code>[target]</code> argument
     */
    CommandFunction(@NotNull String description, @NotNull BiFunction<RepairItem, CommandFunction, FunctionHandler> handlerInitializer, boolean hasTarget) {
        this.path = name().toLowerCase().replace("_", "-");
        this.permission = name().toLowerCase().replace("_", "");
        this.description = description;
        this.handlerInitializer = handlerInitializer;
        this.hasTarget = hasTarget;
    }

    /**
     * Initializes a handler for this function.
     *
     * @param plugin the plugin instance
     * @return the created handler
     */
    @NotNull
    public FunctionHandler initHandler(RepairItem plugin) {
        return handlerInitializer.apply(plugin, this);
    }

    /**
     * Returns the configuration path of the function.
     *
     * @return the configuration path of the function
     */
    @NotNull
    public String getPath() {
        return path;
    }

    /**
     * Returns the permission suffix of the function (<code>repairitem.&lt;permission&gt;.self|other</code>).
     *
     * @return the permission suffix of the function
     */
    @NotNull
    public String getPermission() {
        return permission;
    }

    /**
     * Returns the description of the function.
     *
     * @return the description of the function
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Returns if this function also has a command variant with the <code>[target]</code> argument.
     *
     * @return if this function also has a command variant with the <code>[target]</code> argument
     */
    public boolean hasTarget() {
        return hasTarget;
    }
}