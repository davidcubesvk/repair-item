/*
 * Copyright 2022 https://dejvokep.dev/
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
package dev.dejvokep.repairitem.repair;

import dev.dejvokep.repairitem.RepairItem;
import dev.dejvokep.repairitem.command.CommandFunction;
import dev.dejvokep.repairitem.utils.BlockedItem;
import dev.dejvokep.repairitem.utils.IntRange;
import dev.dejvokep.repairitem.utils.VersionConstants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Class handling all repair operations.
 */
public class Repairer {

    /**
     * Range of the inventory slots.
     */
    private static final IntRange INVENTORY_SLOTS = new IntRange(0, 36);

    /**
     * Range of the armor slots.
     */
    private static final IntRange ARMOR_SLOTS = new IntRange(0, 4);

    /**
     * Range of the hotbar slots.
     */
    private static final IntRange HOTBAR_SLOTS = new IntRange(0, 9);

    private final RepairItem plugin;
    private final Set<BlockedItem> blockedItems = new HashSet<>();

    // Reflection components
    private Class<?> damageableClass;
    private Method setDurabilityMethod, getDurabilityMethod;

    /**
     * Initializes and reloads the repairer.
     *
     * @param plugin the plugin instance
     */
    public Repairer(@NotNull RepairItem plugin) {
        this.plugin = plugin;

        // Initialize reflection components
        try {
            damageableClass = getDamageableClass();
            setDurabilityMethod = getSetDurabilityMethod();
            getDurabilityMethod = getGetDurabilityMethod();
        } catch (ReflectiveOperationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Some of the resources to run the plugin could not be loaded; try restarting the server. If the problem persists, please report it.", ex);
        }

        reload();
    }

    /**
     * Reloads the blocked items.
     */
    public void reload() {
        // Clear
        blockedItems.clear();

        // Reset the set
        plugin.getConfiguration().getOptionalMapList("blocked-items").ifPresent(sections -> {
            for (Map<?, ?> map : sections)
                blockedItems.add(new BlockedItem(plugin, map));
        });
    }

    /**
     * Repairs inventory items in accordance with the given function.
     * <p>
     * If the given function is not a repair function, an {@link IllegalArgumentException} will be thrown.
     *
     * @param player   player whose items to repair
     * @param function function type defining the content to repair
     * @return the result
     */
    @NotNull
    public RepairResult repair(@NotNull Player player, @NotNull CommandFunction function) {
        switch (function) {
            case ALL:
                return repairAll(player);
            case INVENTORY:
                return repairInventory(player);
            case ARMOR:
                return repairArmor(player);
            case HOTBAR:
                return repairHotBar(player);
            case BOTH_HANDS:
                return repairBothHands(player);
            case MAIN_HAND:
                return repairHand(player, true);
            case OFF_HAND:
                return repairHand(player, false);
        }

        throw new IllegalArgumentException("The passed function is not a repair function!");
    }

    /**
     * Repairs all items (inventory and armor contents).
     *
     * @param player the player whose items to repair
     * @return the result
     */
    @NotNull
    public RepairResult repairAll(@NotNull Player player) {
        return repairInventory(player).merge(repairArmor(player));
    }

    /**
     * Repairs all items, excluding the armor.
     *
     * @param player the player whose items to repair
     * @return the result
     */
    @NotNull
    public RepairResult repairInventory(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();

        // Repair both hands
        RepairResult result = repairBothHands(player);
        // Repair the rest
        for (int slot : INVENTORY_SLOTS.getContents())
            result = result.merge(repair(inventory.getItem(slot)));

        return result;
    }

    /**
     * Repairs the armor.
     *
     * @param player the player whose items to repair
     * @return the result
     */
    @NotNull
    public RepairResult repairArmor(@NotNull Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();

        // Repair
        RepairResult result = RepairResult.empty();
        for (int slot : ARMOR_SLOTS.getContents())
            result = result.merge(repair(armor[slot]));

        return result;
    }

    /**
     * Repairs the hotbar items, including both hands.
     *
     * @param player the player whose items to repair
     * @return the result
     */
    @NotNull
    public RepairResult repairHotBar(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();

        // Repair both hands
        RepairResult result = repairBothHands(player);
        // Repair the rest
        for (int slot : HOTBAR_SLOTS.getContents())
            result = result.merge(repair(inventory.getItem(slot)));

        return result;
    }

    /**
     * Repairs both hands (off-hand only if supported).
     *
     * @param player the player whose items to repair
     * @return the result
     */
    @NotNull
    public RepairResult repairBothHands(@NotNull Player player) {
        return repairHand(player, true).merge(repairHand(player, true));
    }

    /**
     * Repairs item in a hand.
     *
     * @param player the player whose items to repair
     * @param main   if to repair item in the main-hand (<code>false</code> for off-hand)
     * @return the result
     */
    @SuppressWarnings("deprecation")
    @NotNull
    public RepairResult repairHand(@NotNull Player player, boolean main) {
        if (main)
            return repair(VersionConstants.OFF_HAND_UNSUPPORTED ? player.getInventory().getItemInHand() : player.getInventory().getItemInMainHand());

        if (VersionConstants.OFF_HAND_UNSUPPORTED)
            return RepairResult.error(RepairResult.Status.ERROR_UNSUPPORTED);

        return repair(player.getInventory().getItemInOffHand());
    }

    /**
     * Repairs the given item.
     *
     * @param itemStack the item to repair
     * @return the result
     */
    @NotNull
    public RepairResult repair(@Nullable ItemStack itemStack) {
        // Reflection components not initialized
        if (getDurabilityMethod == null || setDurabilityMethod == null)
            return RepairResult.error(RepairResult.Status.ERROR_UNKNOWN);
        // Cannot be repaired
        if (itemStack == null || itemStack.getType() == null || itemStack.getType() == Material.AIR || itemStack.getType().getMaxDurability() == 0)
            return RepairResult.error(RepairResult.Status.ERROR_NOT_REPAIRED);
        // Check blocked items
        for (BlockedItem blocked : blockedItems)
            if (blocked.compare(itemStack))
                return RepairResult.error(RepairResult.Status.ERROR_NOT_REPAIRED);

        try {
            // Use ItemStack methods for versions < 1.13, damageable interface methods otherwise
            if (VersionConstants.LEGACY_DURABILITY) {
                if (((Short) getDurabilityMethod.invoke(itemStack)) == 0)
                    return RepairResult.error(RepairResult.Status.ERROR_NOT_REPAIRED);
                setDurabilityMethod.invoke(itemStack, (short) 0);
            } else {
                if (damageableClass == null)
                    return RepairResult.error(RepairResult.Status.ERROR_UNKNOWN);

                // Cast
                Object damageable = damageableClass.cast(itemStack.getItemMeta());
                // Repair if damaged
                if (((Integer) getDurabilityMethod.invoke(damageable)) == 0)
                    return RepairResult.error(RepairResult.Status.ERROR_NOT_REPAIRED);
                setDurabilityMethod.invoke(damageable, 0);

                // Set back
                itemStack.setItemMeta((ItemMeta) damageable);
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to repair an item! If the problem persists, please report it.", ex);
            return RepairResult.error(RepairResult.Status.ERROR_UNKNOWN);
        }

        // Success
        return RepairResult.success();
    }

    /**
     * Returns reference of the method for setting the durability of an item according to the current server version in
     * use.
     *
     * @return the reference of the method for setting the durability of an item
     * @throws NoSuchMethodException if the method could not be found
     */
    @NotNull
    private Method getSetDurabilityMethod() throws NoSuchMethodException {
        return VersionConstants.LEGACY_DURABILITY ? ItemStack.class.getDeclaredMethod("setDurability", short.class) : damageableClass.getDeclaredMethod("setDamage", int.class);
    }

    /**
     * Returns reference of the method for getting the durability of an item according to the current server version in
     * use.
     *
     * @return the reference of the method for getting the durability of an item
     * @throws NoSuchMethodException if the method could not be found
     */
    @NotNull
    private Method getGetDurabilityMethod() throws NoSuchMethodException {
        return VersionConstants.LEGACY_DURABILITY ? ItemStack.class.getDeclaredMethod("getDurability") : damageableClass.getDeclaredMethod("getDamage");
    }

    /**
     * Returns the damageable interface reference for item durability management. Returns <code>null</code> if running
     * an unsupported server version (older than 1.13).
     *
     * @return the damageable interface reference, or <code>null</code> if running an unsupported server version
     * @throws ClassNotFoundException if the class could not be found
     */
    @Nullable
    private Class<?> getDamageableClass() throws ClassNotFoundException {
        return VersionConstants.LEGACY_DURABILITY ? null : Class.forName("org.bukkit.inventory.meta.Damageable");
    }

}