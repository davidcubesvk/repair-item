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
import dev.dejvokep.repairitem.command.CommandRegistrar;
import dev.dejvokep.repairitem.command.CommandFunction;
import dev.dejvokep.repairitem.utils.BlockedItem;
import dev.dejvokep.repairitem.utils.IntRange;
import dev.dejvokep.repairitem.utils.Versioner;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Class handling all repair operations.
 */
public class Repairer {

    /**
     * Range of inventory slots.
     */
    private static final IntRange INVENTORY_RANGE = new IntRange(0, 36);

    /**
     * Range of armor slots.
     */
    private static final IntRange ARMOR_RANGE = new IntRange(0, 4);

    /**
     * Range of hot-bar slots.
     */
    private static final IntRange HOT_BAR = new IntRange(0, 9);


    //The Damageable interface (for backwards compatibility).
    private Class<?> damageableClass;
    //The method setting the durability of an item (for backwards compatibility).
    private Method setDurabilityMethod;
    //The method getting the durability of an item (for backwards compatibility).
    private Method getDurabilityMethod;
    //If off hand is supported and if using the old durability system
    private final boolean offHandSupported, oldDurabilitySystem;

    //The plugin instance
    private final RepairItem plugin;
    //Blocked items
    private final Set<BlockedItem> blockedItems = new HashSet<>();

    /**
     * Initializes the repairer with the given main class.
     *
     * @param plugin the main plugin class
     */
    public Repairer(RepairItem plugin) {
        //Set the plugin
        this.plugin = plugin;

        //If off hand is supported
        this.offHandSupported = Versioner.isNewerOr(Versioner.V1_9);
        //If using the old durability system
        this.oldDurabilitySystem = Versioner.isOlderThan(Versioner.V1_13);
        //Initialize the fields
        try {
            damageableClass = getDamageableClass();
            setDurabilityMethod = getSetDurabilityMethod();
            getDurabilityMethod = getGetDurabilityMethod();
        } catch (ReflectiveOperationException ex) {
            //Log
            plugin.getLogger().log(Level.SEVERE, "Some of the resources to run the plugin could not be loaded; please restart the server. If the problem persists, please report it.", ex);
            //Set all to null
            damageableClass = null;
            setDurabilityMethod = getDurabilityMethod = null;
        }
    }

    /**
     * Reloads the blocked items.
     */
    public void reload() {
        //Clear
        blockedItems.clear();

        //Get the list
        List<?> sections = plugin.getConfiguration().getList("blocked-items");
        //If it is null or does not contain configuration sections
        if (sections == null || !(sections.get(0) instanceof LinkedHashMap))
            return;
        //Loop through all items
        for (Object section : sections)
            blockedItems.add(new BlockedItem((LinkedHashMap<String, Object>) section));
    }

    /**
     * Repairs by the given content. If the specified content does not represent a content (e.g.
     * {@link CommandRegistrar.Function#HELP},
     * {@link CommandRegistrar.Function#RELOAD} or <code>null</code>, the method returns
     * <code>null</code>.
     *
     * @param player  the player to run the repair for
     * @param function the content to repair
     * @return the repair result
     */
    public RepairResult repair(Player player, CommandFunction function) {
        //Switch
        switch (function) {
            case ALL:
                return repairAll(player);
            case INVENTORY:
                return repairInventory(player);
            case ARMOR:
                return repairArmor(player);
            case HOT_BAR:
                return repairHotBar(player);
            case BOTH_HANDS:
                return repairBothHands(player);
            case MAIN_HAND:
                return repairHand(player, true);
            case OFF_HAND:
                return repairHand(player, false);
        }

        return null;
    }

    /**
     * Repairs everything - e.g. inventory and armor.
     *
     * @param player the player to run the repair for
     * @return the repair result
     */
    public RepairResult repairAll(Player player) {
        //Repair
        byte repaired = (byte) (repairInventory(player).getRepaired() + repairArmor(player).getRepaired());
        //Return
        return new RepairResult(repaired > 0 ? RepairResult.Status.SUCCESS : RepairResult.Status.FAIL_GENERALIZED, repaired);
    }

    /**
     * Repairs the inventory (without the armor) including both hands.
     *
     * @param player the player to run the repair for
     * @return the repair result
     */
    public RepairResult repairInventory(Player player) {
        //The inventory
        PlayerInventory inventory = player.getInventory();
        //Amount of repaired items
        byte repaired = 0;

        //Go through all items
        for (int slot : INVENTORY_RANGE.getContents())
            //Repair and count the repaired items
            repaired += repairItem(inventory.getItem(slot)).getRepaired();

        //Repair both hands
        repaired += repairHand(player, false).getRepaired();

        //Return
        return new RepairResult(repaired > 0 ? RepairResult.Status.SUCCESS : RepairResult.Status.FAIL_GENERALIZED, repaired);
    }

    /**
     * Repairs the armor.
     *
     * @param player the player to run the repair for
     * @return the repair result
     */
    public RepairResult repairArmor(Player player) {
        //The armor contents
        ItemStack[] armor = player.getInventory().getArmorContents();
        //Amount of repaired items
        byte repaired = 0;

        //Go through all items
        for (int slot : ARMOR_RANGE.getContents())
            //Repair and count the repaired items
            repaired += repairItem(armor[slot]).getRepaired();

        //Return
        return new RepairResult(repaired > 0 ? RepairResult.Status.SUCCESS : RepairResult.Status.FAIL_GENERALIZED, repaired);
    }

    /**
     * Repairs the hot-bar including both hands.
     *
     * @param player the player to run the repair for
     * @return the repair result
     */
    public RepairResult repairHotBar(Player player) {
        //The inventory
        PlayerInventory inventory = player.getInventory();
        //Amount of repaired items
        byte repaired = 0;

        //Go through all items
        for (int slot : HOT_BAR.getContents())
            //Repair and count the repaired items
            repaired += repairItem(inventory.getItem(slot)).getRepaired();
        //Repair the off-hand
        repaired += repairHand(player, false).getRepaired();

        //Return
        return new RepairResult(repaired > 0 ? RepairResult.Status.SUCCESS : RepairResult.Status.FAIL_GENERALIZED, repaired);
    }

    /**
     * Repairs both hands (off hand only if supported).
     *
     * @param player the player to run the repair for
     * @return the repair result
     */
    public RepairResult repairBothHands(Player player) {
        //Repair
        byte repaired = (byte) (repairHand(player, true).getRepaired() + repairHand(player, false).getRepaired());
        //Return
        return new RepairResult(repaired > 0 ? RepairResult.Status.SUCCESS : RepairResult.Status.FAIL_GENERALIZED, repaired);
    }

    /**
     * Repairs hand of the specified type.
     *
     * @param player the player to run the repair for
     * @return the repair result
     */
    public RepairResult repairHand(Player player, boolean main) {
        //If repairing main hand
        if (main) {
            return repairItem(offHandSupported ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInHand());
        } else {
            //If not supported
            if (!offHandSupported)
                return new RepairResult(RepairResult.Status.FAIL_UNSUPPORTED);
            else
                return repairItem(player.getInventory().getItemInOffHand());
        }
    }

    /**
     * Repairs the given item.
     *
     * @param itemStack the item to repair
     * @return the repair result
     */
    public RepairResult repairItem(ItemStack itemStack) {
        //If no item is present
        if (itemStack == null || itemStack.getType() == null || itemStack.getType() == Material.AIR)
            return new RepairResult(RepairResult.Status.FAIL_NO_ITEM);
        //If the item can not be repaired
        if (itemStack.getType().getMaxDurability() == 0)
            return new RepairResult(RepairResult.Status.FAIL_UNREPAIRABLE);
        //Loop through all blocked items
        for (BlockedItem blocked : blockedItems)
            //If the item is blocked
            if (blocked.compare(itemStack))
                return new RepairResult(RepairResult.Status.FAIL_BLOCKED);

        try {
            //If get/set durability method is null
            if (getDurabilityMethod == null || setDurabilityMethod == null)
                return new RepairResult(RepairResult.Status.FAIL_OTHER);

            //If version is older than 1.13
            if (oldDurabilitySystem) {
                //If already repaired
                if (((Short) getDurabilityMethod.invoke(itemStack)) == 0)
                    return new RepairResult(RepairResult.Status.FAIL_ALREADY_REPAIRED);
                //Use only the stack's method
                setDurabilityMethod.invoke(itemStack, (short) 0);
            } else {
                //If damageable class is null
                if (damageableClass == null)
                    return new RepairResult(RepairResult.Status.FAIL_OTHER);

                //Cast to the damageable
                Object damageable = damageableClass.cast(itemStack.getItemMeta());
                //If already repaired
                if (((Integer) getDurabilityMethod.invoke(damageable)) == 0)
                    return new RepairResult(RepairResult.Status.FAIL_ALREADY_REPAIRED);
                //Cast the item meta to damageable and then set damage
                setDurabilityMethod.invoke(damageable, 0);
                //Set the item meta
                itemStack.setItemMeta((ItemMeta) damageable);
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            //Log
            plugin.getLogger().log(Level.SEVERE, "Failed to repair an item! If the problem persists, please report it.", ex);
            return new RepairResult(RepairResult.Status.FAIL_OTHER);
        }

        //Repaired
        return new RepairResult(RepairResult.Status.SUCCESS);
    }

    /**
     * Returns the method for setting the durability of an item, or <code>null</code> if an error occurred.
     *
     * @return the method for setting the durability of an item, or <code>null</code>
     * @throws NoSuchMethodException if the method could not be found
     */
    private Method getSetDurabilityMethod() throws NoSuchMethodException {
        //Return by version
        if (oldDurabilitySystem)
            return ItemStack.class.getDeclaredMethod("setDurability", short.class);
        else
            return damageableClass.getDeclaredMethod("setDamage", int.class);
    }

    /**
     * Returns the method for getting the durability of an item, or <code>null</code> if an error occurred.
     *
     * @return the method for getting the durability of an item, or <code>null</code>
     * @throws NoSuchMethodException if the method could not be found
     */
    private Method getGetDurabilityMethod() throws NoSuchMethodException {
        //Return by version
        if (oldDurabilitySystem)
            return ItemStack.class.getDeclaredMethod("getDurability");
        else
            return damageableClass.getDeclaredMethod("getDamage");
    }

    /**
     * Returns the damageable interface for getting/setting the durability of an item, or <code>null</code> if an error
     * occurred/using version older than 1.13.
     *
     * @return the damageable interface for getting/setting the durability of an item, or <code>null</code> if an error
     * occurred/unsupported
     * @throws ClassNotFoundException if the class could not be found
     */
    private Class<?> getDamageableClass() throws ClassNotFoundException {
        //Return by version
        if (oldDurabilitySystem)
            return null;
        else
            return Class.forName("org.bukkit.inventory.meta.Damageable");
    }

}