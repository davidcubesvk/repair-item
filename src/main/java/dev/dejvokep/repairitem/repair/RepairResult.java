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

/**
 * A result of a repair operation.
 */
public class RepairResult {

    /**
     * Status of the operation.
     */
    public enum Status {
        /**
         * Succeeded (at least one item was repaired).
         */
        SUCCESS(null),
        /**
         * Failed because of an error (generalized message).
         */
        FAIL_GENERALIZED("generalized"),
        /**
         * Failed because no item was found.
         */
        FAIL_NO_ITEM("specific.no-repairable-item"),
        /**
         * Failed because no repairable item was found.
         */
        FAIL_UNREPAIRABLE("specific.no-repairable-item"),
        /**
         * Failed because all items are blocked.
         */
        FAIL_BLOCKED("specific.no-repairable-item"),
        /**
         * Failed because all items are already repaired.
         */
        FAIL_ALREADY_REPAIRED("specific.no-repairable-item"),
        /**
         * Failed because the operation is not supported.
         */
        FAIL_UNSUPPORTED("specific.unsupported"),
        /**
         * failed because of other error.
         */
        FAIL_OTHER("specific.other");

        //The path
        private final String path;

        /**
         * Initializes the path.
         *
         * @param path the configuration file message path (in root section <code>fail</code>)
         */
        Status(String path) {
            this.path = path;
        }

        /**
         * Returns the configuration file message path of the status.
         *
         * @return the configuration file message path of the status
         */
        public String getPath() {
            return path;
        }
    }

    //Status
    private final Status status;
    //Amount of items repaired
    private final byte repaired;

    /**
     * Initializes the result with the given status and amount of items repaired.
     *
     * @param status   the status of the operation
     * @param repaired the amount of items repaired
     */
    RepairResult(Status status, byte repaired) {
        this.status = status;
        this.repaired = repaired;
    }

    /**
     * Initializes the result with the given status. Calls the {@link #RepairResult(Status, byte)} constructor. If the
     * status is {@link Status#SUCCESS}, repaired amount of items is automatically set to <code>1</code> (otherwise
     * <code>0</code>).
     *
     * @param status the status of the operation
     */
    RepairResult(Status status) {
        this(status, status == Status.SUCCESS ? (byte) 1 : (byte) 0);
    }

    /**
     * Returns the status of the operation.
     *
     * @return the status of the operation
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the amount of items repaired.
     *
     * @return the amount of items repaired
     */
    public byte getRepaired() {
        return repaired;
    }
}
