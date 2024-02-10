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
package dev.dejvokep.repairitem.utils;

import dev.dejvokep.repairitem.RepairItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 * Class representing a blocked item.
 */
public class BlockedItem {

    /**
     * Path to the item's type.
     */
    public static final String PATH_TYPE = "type";

    /**
     * Path to the item's name.
     */
    public static final String PATH_NAME = "name";

    /**
     * Path to the item's lore.
     */
    public static final String PATH_LORE = "lore";

    /**
     * Path to the item's enchantments.
     */
    public static final String PATH_ENCHANTMENTS = "enchantments";

    /**
     * Path to the item's flags.
     */
    public static final String PATH_FLAGS = "flags";

    /**
     * Path to the item's unbreakable state.
     */
    public static final String PATH_UNBREAKABLE = "unbreakable";

    // Properties
    private Material type;
    private String name;
    private final List<String> lore = new ArrayList<>();
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final Set<ItemFlag> flags = new HashSet<>();
    private boolean unbreakable;

    // Property flags
    private boolean typeSet = false, nameSet = false, loreSet = false, enchantmentsSet = false, flagsSet = false,
            unbreakableSet = false, metaDependent = false;

    /**
     * Creates a blocked item using the properties stored in the given map. The property key definitions must adhere to
     * the format defined by the class constants.
     *
     * @param plugin  the plugin instance, used only for logging
     * @param section a section map representing containing the properties
     */
    public BlockedItem(RepairItem plugin, Map<?, ?> section) {
        try {
            // Type
            if (section.containsKey(PATH_TYPE)) {
                type = Material.valueOf((String) section.get(PATH_TYPE));
                typeSet = true;
            }

            // Name
            if (section.containsKey(PATH_NAME)) {
                name = (String) section.get(PATH_NAME);
                metaDependent = nameSet = true;
            }

            // Lore
            if (section.containsKey(PATH_LORE)) {
                ((Collection<?>) section.get(PATH_LORE)).forEach(line -> this.lore.add(line.toString()));
                metaDependent = loreSet = true;
            }

            // Enchantments
            if (section.containsKey(PATH_ENCHANTMENTS)) {
                Collection<?> enchantments = (Collection<?>) section.get(PATH_ENCHANTMENTS);
                for (Object enchantment : enchantments) {
                    String[] data = enchantment.toString().split(":");
                    this.enchantments.put(getEnchantment(data[0]), Integer.valueOf(data[1]));
                }
                metaDependent = enchantmentsSet = true;
            }

            // Flags
            if (section.containsKey(PATH_FLAGS)) {
                ((Collection<?>) section.get(PATH_FLAGS)).forEach(flag -> this.flags.add(ItemFlag.valueOf(flag.toString())));
                metaDependent = flagsSet = true;
            }

            // Unbreakable
            if (section.containsKey(PATH_UNBREAKABLE)) {
                unbreakable = (boolean) section.get(PATH_UNBREAKABLE);
                metaDependent = unbreakableSet = true;
            }
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Some of the blocked items could not be loaded; please reload the plugin and check the configuration. If the problem persists, please report it.", ex);
        }
    }

    /**
     * Returns an enchantment by the given name, in accordance to the constants defined by {@link Enchantment}.
     *
     * @param name the name of the enchantment (automatically upper-cased and spaces replaced by underscores)
     * @return the enchantment by the given name
     * @throws ReflectiveOperationException a reflective operation exception
     */
    public Enchantment getEnchantment(String name) throws ReflectiveOperationException {
        Field field = Enchantment.class.getDeclaredField(name.toUpperCase().replace(" ", "_"));
        field.setAccessible(true);
        return (Enchantment) field.get(null);
    }

    /**
     * Compares the given item and returns whether it should (is) be blocked (it's properties are equal to the blocked
     * item's) <code>true</code>, otherwise <code>false</code>.
     *
     * @param itemStack the item to compare
     * @return whether the given item should (is) blocked
     */
    public boolean compare(ItemStack itemStack) {
        // Type
        if (typeSet && type != itemStack.getType())
            return false;

        // Comparing item metas
        if (metaDependent) {
            // Meta
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null)
                return false;

            // Name
            if (nameSet && (!itemMeta.hasDisplayName() || !name.equals(itemMeta.getDisplayName())))
                return false;
            // Lore
            if (loreSet && (!itemMeta.hasLore() || !lore.equals(itemMeta.getLore())))
                return false;
            // Enchantments
            if (enchantmentsSet && (!itemMeta.hasEnchants() || !compareMaps(enchantments, itemMeta.getEnchants())))
                return false;
            // Flags
            if (flagsSet && !flags.equals(itemMeta.getItemFlags()))
                return false;
            // Unbreakable
            if (unbreakableSet && unbreakable != itemMeta.isUnbreakable())
                return false;
        }

        // Blocked
        return true;
    }

    /**
     * Compares the given maps and returns whether the maps are equal. Two maps are considered equal by this method if
     * both contain the same <code>key=value</code> pairs.
     *
     * @param a   the first of the maps to compare
     * @param b   the second of the maps to compare
     * @param <K> key type
     * @param <V> value type
     * @return if the maps are equal
     */
    private <K, V> boolean compareMaps(Map<K, V> a, Map<K, V> b) {
        if (a.size() != b.size())
            return false;
        for (K key : a.keySet())
            if (!b.containsKey(key) || !a.get(key).equals(b.get(key)))
                return false;
        return true;
    }
}