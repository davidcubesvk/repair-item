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

/**
 * An API class used to identify the currently used server version for correct server API usage.
 */
public class Versioner {

    //Version constants
    public static final byte V1_7 = 7, V1_8 = 8, V1_9 = 9, V1_10 = 10, V1_11 = 11, V1_12 = 12, V1_13 = 13, V1_14 = 14,
            V1_15 = 15, V1_16 = 16;

    //The current version
    private static final byte version = loadVersion();

    /**
     * Returns if the server version equals the given version. It is necessary to use byte constants provided.
     *
     * @param version the version byte
     * @return if the server version equals the given version
     */
    public static boolean is(byte version) {
        return Versioner.version == version;
    }

    /**
     * Returns if the server version is newer than the given version. It is necessary to use byte constants provided.
     *
     * @param version the version byte
     * @return if the server version is newer than the given version
     */
    public static boolean isNewerThan(byte version) {
        return Versioner.version > version;
    }

    /**
     * Returns if the server version is older than the given version. It is necessary to use byte constants provided.
     *
     * @param version the version byte
     * @return if the server version is older than the given version
     */
    public static boolean isOlderThan(byte version) {
        return Versioner.version < version;
    }

    /**
     * Returns if the server version is newer or equals the given version. It is necessary to use byte constants
     * provided.
     *
     * @param version the version byte
     * @return if the server version is newer or equals the given version
     */
    public static boolean isNewerOr(byte version) {
        return Versioner.version >= version;
    }

    /**
     * Returns if the server version is older or equals the given version. It is necessary to use byte constants
     * provided.
     *
     * @param version the version byte
     * @return if the server version is older or equals the given version
     */
    public static boolean isOlderOr(byte version) {
        return Versioner.version <= version;
    }

    /**
     * Loads and returns the server version byte.
     * @return the server version byte
     */
    private static byte loadVersion() {
        //The server version
        String serverVersion = Bukkit.getBukkitVersion();

        //Assign the version byte
        if (serverVersion.contains("1.7"))
            return V1_7;
        else if (serverVersion.contains("1.8"))
            return V1_8;
        else if (serverVersion.contains("1.9"))
            return V1_9;
        else if (serverVersion.contains("1.10"))
            return V1_10;
        else if (serverVersion.contains("1.11"))
            return V1_11;
        else if (serverVersion.contains("1.12"))
            return V1_12;
        else if (serverVersion.contains("1.13"))
            return V1_13;
        else if (serverVersion.contains("1.14"))
            return V1_14;
        else if (serverVersion.contains("1.15"))
            return V1_15;
        else if (serverVersion.contains("1.16"))
            return V1_16;

        //Not found
        return -1;
    }

}