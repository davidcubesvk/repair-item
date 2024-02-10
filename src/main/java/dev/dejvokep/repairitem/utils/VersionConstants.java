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
package dev.dejvokep.repairitem.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;

/**
 * Version constants class used to determine availability of parts of the server API.
 */
public class VersionConstants {

    /**
     * Constant representing if the off-hand is unsupported (if the sever version is older than 1.9).
     */
    public static final boolean OFF_HAND_UNSUPPORTED = is(new String[]{"1.7", "1.8"});

    /**
     * Constant representing if the legacy durability system is in use (if the sever version is older than 1.13).
     */
    public static final boolean LEGACY_DURABILITY = is(new String[]{"1.7", "1.8", "1.9", "1.10", "1.11", "1.12"});

    /**
     * Returns if the server's version is contained within the given versions array.
     *
     * @param versions the versions to compare with
     * @return if the server's version is contained within the given versions array
     */
    private static boolean is(String[] versions) {
        return Arrays.stream(versions).map(v -> Bukkit.getBukkitVersion().contains(v)).reduce(false, (result, e) -> result || e);
    }

}