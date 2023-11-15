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

/**
 * A range of integers specified by min and max integers.
 */
public class IntRange {

    //The contents
    private final int[] contents;

    /**
     * Initializes the range by the given min (inclusive) and max (exclusive) integer. Max integer has to be greater
     * than the min integer, or the range will not initialize it's contents.
     *
     * @param min the min integer (inclusive)
     * @param max the max integer (exclusive)
     */
    public IntRange(int min, int max) {
        //If max is not greater than min
        if (min >= max) {
            //Initialize
            contents = null;
            //Return
            return;
        }

        //Create the array
        contents = new int[max - min];
        //Go through all indexes
        for (int index = 0; min < max; index++) {
            //Set the content integer
            contents[index] = min;
            //Increase the min (content) integer
            min++;
        }
    }

    /**
     * Returns the contents of this range.
     *
     * @return the contents of this range
     */
    public int[] getContents() {
        return contents;
    }

}