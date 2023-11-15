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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 * Class representing a blocked item.
 */
public class BlockedItem {

    /**
     * Configuration file path to the item's type.
     */
    public static final String PATH_TYPE = "type";

    /**
     * Configuration file path to the item's name.
     */
    public static final String PATH_NAME = "name";

    /**
     * Configuration file path to the item's lore.
     */
    public static final String PATH_LORE = "lore";

    /**
     * Configuration file path to the item's enchantments.
     */
    public static final String PATH_ENCHANTMENTS = "enchantments";

    /**
     * Configuration file path to the item's flags.
     */
    public static final String PATH_FLAGS = "flags";

    /**
     * Configuration file path to the item's unbreakable state.
     */
    public static final String PATH_UNBREAKABLE = "unbreakable";

    /**
     * The method used to get enchantment by keys.
     *
     * @see #getGetEnchantmentByKeyMethod()
     */
    public static final Method getEnchantmentByKeyMethod = getGetEnchantmentByKeyMethod();

    /**
     * The method used to get the game's namespace.
     *
     * @see #getGameNamespaceMethod()
     */
    public static final Method gameNamespaceMethod = getGameNamespaceMethod();

    //Type
    private Material type;
    //Name
    private String name;
    //Lore
    private final List<String> lore = new ArrayList<>();
    //Enchantments and levels
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    //Flags
    private final Set<ItemFlag> flags = new HashSet<>();
    //Unbreakable state
    private boolean unbreakable;

    //If each property is set and whether the item's comparing is meta-dependent
    private boolean typeSet = false, nameSet = false, loreSet = false, enchantmentsSet = false, flagsSet = false,
            unbreakableSet = false, metaDependent = false;

    //If to use the old enchantment naming
    private final boolean oldEnchantments;

    /**
     * Initializes the blocked item with the given map, in which the item properties are.
     *
     * @param section a map representing the configuration section containing the properties (directly)
     */
    public BlockedItem(LinkedHashMap<String, Object> section) {
        //If to use the old enchantment naming
        this.oldEnchantments = Versioner.isOlderThan(Versioner.V1_13);

        try {
            //If type is set
            if (section.containsKey(PATH_TYPE)) {
                //Get
                type = Material.valueOf((String) section.get(PATH_TYPE));
                //Is set
                typeSet = true;
            }

            //If name is set
            if (section.containsKey(PATH_NAME)) {
                //Get
                name = (String) section.get(PATH_NAME);
                //Is set
                metaDependent = nameSet = true;
            }

            //If lore is set
            if (section.containsKey(PATH_LORE)) {
                //Cast to iterable
                Iterable<?> lore = (Iterable<?>) section.get(PATH_LORE);
                //Go through all lore lines
                for (Object line : lore)
                    //Add
                    this.lore.add((String) line);
                //Is set
                metaDependent = loreSet = true;
            }

            //If enchantments are set
            if (section.containsKey(PATH_ENCHANTMENTS)) {
                //Cast to iterable
                Iterable<?> enchantments = (Iterable<?>) section.get(PATH_ENCHANTMENTS);
                //The enchantment data (reused)
                String[] data;
                //Go through all enchantments
                for (Object enchantment : enchantments) {
                    //Split the data
                    data = ((String) enchantment).split(":");
                    //Put into the map
                    this.enchantments.put(getEnchantment(data[0]), Integer.valueOf(data[1]));
                }
                //Is set
                metaDependent = enchantmentsSet = true;
            }

            //If flags are set
            if (section.containsKey(PATH_FLAGS)) {
                //Cast to iterable
                Iterable<?> itemFlags = (Iterable<?>) section.get(PATH_FLAGS);
                //Go through all flags
                for (Object flag : itemFlags)
                    flags.add(ItemFlag.valueOf((String) flag));
                //Is set
                metaDependent = flagsSet = true;
            }

            //If unbreakable is set
            if (section.containsKey(PATH_UNBREAKABLE)) {
                //Get
                unbreakable = (Boolean) section.get(PATH_UNBREAKABLE);
                //Is set
                metaDependent = unbreakableSet = true;
            }
        } catch (Exception ex) {
            //Log
            Bukkit.getLogger().log(Level.SEVERE, "[RepairItem] Some of the blocked items could not be loaded; please reload the plugin and check the configuration. If the problem persists, please report it.", ex);
        }
    }

    /**
     * Returns an enchantment by the given name.
     * <p>
     * If any reflection methods failed to load, returns <code>null</code>. Please note that there is an enchantment
     * naming change between these two versions.
     *
     * @param name the name of the enchantment (if using server version older than 1.9, it is automatically upper-cased,
     *             otherwise lower-cased and spaces are replaced by underscores)
     * @return the enchantment by the given name, or <code>null</code>
     * @throws IllegalAccessException    if an error occurred in reflection method invocation
     * @throws InvocationTargetException if an error occurred in reflection method invocation
     */
    public Enchantment getEnchantment(String name) throws IllegalAccessException, InvocationTargetException {
        //If the methods are null
        if (!oldEnchantments && (getEnchantmentByKeyMethod == null || gameNamespaceMethod == null))
            return null;

        return oldEnchantments ? Enchantment.getByName(name.toUpperCase()) : (Enchantment) getEnchantmentByKeyMethod.invoke(null, gameNamespaceMethod.invoke(null, name.toLowerCase().replace(" ", "_")));
    }

    /**
     * Compares the given item and returns whether it should (is) be blocked (it's properties are equal to the blocked
     * item's) <code>true</code>, otherwise <code>false</code>.
     *
     * @param itemStack the item to compare
     * @return whether the given item should (is) blocked
     */
    public boolean compare(ItemStack itemStack) {
        //If the comparing is meta-dependent and item meta is not present
        if (metaDependent && !itemStack.hasItemMeta())
            return false;

        //If type is set and they do not equal
        if (typeSet && type != itemStack.getType())
            return false;

        //If comparing item metas
        if (metaDependent) {
            //The item meta
            ItemMeta itemMeta = itemStack.getItemMeta();

            //If the name is set and they do not equal
            if (nameSet && (!itemMeta.hasDisplayName() || !name.equals(itemMeta.getDisplayName())))
                return false;
                //If the lore is set and they do not equal
            else if (loreSet && (!itemMeta.hasLore() || !lore.equals(itemMeta.getLore())))
                return false;
                //If the enchantments are set and they do not equal
            else if (enchantmentsSet && (!itemMeta.hasEnchants() || !enchantments.equals(itemMeta.getEnchants())))
                return false;
                //If the flags are set and they do not equal
            else if (flagsSet && !flags.equals(itemMeta.getItemFlags()))
                return false;
                //If the unbreakable is set and they do not equal
            else if (unbreakableSet && unbreakable != itemMeta.isUnbreakable())
                return false;
        }

        //Block the item
        return true;
    }


    /**
     * Returns the <code>getByKey</code> method from the {@link Enchantment} class, or <code>null</code> if an error
     * occurred/not supported (server versions 1.12 and older).
     *
     * @return the method from the {@link Enchantment} class, or <code>null</code>
     */
    private static Method getGetEnchantmentByKeyMethod() {
        //If not supported
        if (Versioner.isOlderThan(Versioner.V1_13))
            return null;

        try {
            //Return the method
            return Class.forName("org.bukkit.enchantments.Enchantment").getDeclaredMethod("getByKey", Class.forName("org.bukkit.NamespacedKey"));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            //Log
            Bukkit.getLogger().log(Level.SEVERE, "[RepairItem] Some of the resources to run the plugin could not be loaded; please restart the server. If the problem persists, please report it.", ex);
            //Return null
            return null;
        }
    }

    /**
     * Returns the game's namespace, or <code>null</code> if an error occurred/not supported (server versions 1.12 and
     * older).
     *
     * @return the game's namespace, or <code>null</code>
     */
    private static Method getGameNamespaceMethod() {
        //If not supported
        if (Versioner.isOlderThan(Versioner.V1_13))
            return null;

        try {
            //Return the method
            return Class.forName("org.bukkit.NamespacedKey").getDeclaredMethod("minecraft", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            //Log
            Bukkit.getLogger().log(Level.SEVERE, "[RepairItem] Some of the resources to run the plugin could not be loaded; please restart the server. If the problem persists, please report it.", ex);
            //Return null
            return null;
        }
    }
}