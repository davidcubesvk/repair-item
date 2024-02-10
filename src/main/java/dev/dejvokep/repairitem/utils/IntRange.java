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

import org.jetbrains.annotations.NotNull;

/**
 * A range of integers defined by min and max boundaries.
 */
public class IntRange {

    private final int[] contents;

    /**
     * Initializes the range by the given min (inclusive) and max (exclusive) integer.
     * <p>
     * The range cannot be invalid - it has to contain at least one element. If <code>min &gt;= max</code>, an
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param min the min integer (inclusive)
     * @param max the max integer (exclusive)
     */
    public IntRange(int min, int max) {
        // Validate boundaries
        if (min >= max)
            throw new IllegalArgumentException("Min must be less than the max range boundary!");

        // Initialize the array
        contents = new int[max - min];
        for (int i = 0; min < max; i++)
            contents[i] = min++;
    }

    /**
     * Returns the contents of this range.
     *
     * @return the contents of this range
     */
    @NotNull
    public int[] getContents() {
        return contents;
    }

}