package dev.dejvokep.repairitem.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;

public class VersionConstants {

    public static final boolean OFF_HAND_UNSUPPORTED = is(new String[]{"1.7", "1.8"});
    public static final boolean LEGACY_DURABILITY = is(new String[]{"1.7", "1.8", "1.9", "1.10", "1.11", "1.12"});

    private static boolean is(String[] versions) {
        return Arrays.stream(versions).map(v -> Bukkit.getBukkitVersion().contains(v)).reduce(false, (result, e) -> result || e);
    }

}