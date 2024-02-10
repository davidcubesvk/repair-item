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
package dev.dejvokep.repairitem.repair;

import dev.dejvokep.repairitem.command.function.CommandFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Result of a repair operation representation.
 */
public class RepairResult {

    /**
     * Status of the operation.
     */
    public enum Status {
        /**
         * Succeeded (at least one item was repaired).
         */
        SUCCESS(CommandFunction::getPath),
        /**
         * No items could be repaired.
         */
        ERROR_NOT_REPAIRED(function -> "error.not-repaired"),
        /**
         * Trying to run an unsupported repair function.
         */
        ERROR_UNSUPPORTED(function -> "error.unsupported"),
        /**
         * An unknown error occurred.
         */
        ERROR_UNKNOWN(function -> "error.unknown");

        private final Function<CommandFunction, String> pathConstructor;

        /**
         * Initializes the status.
         *
         * @param pathConstructor a constructor for the corresponding function status message path
         */
        Status(@NotNull Function<CommandFunction, String> pathConstructor) {
            this.pathConstructor = pathConstructor;
        }

        /**
         * Returns the status message path for the given function.
         *
         * @return the status message path for the given function
         */
        @NotNull
        public String getPath(@NotNull CommandFunction function) {
            return pathConstructor.apply(function);
        }
    }

    private final Status status;
    private final int repaired;

    /**
     * Initializes the result with the given status and amount of items repaired.
     *
     * @param status   the status
     * @param repaired the amount of items repaired, must not be negative
     */
    private RepairResult(@Nullable Status status, int repaired) {
        if (repaired < 0)
            throw new IllegalArgumentException("Repaired items count cannot be less than 0!");

        this.status = status;
        this.repaired = repaired;
    }

    /**
     * Merges this with the given result.
     * <p>
     * The status of the returned result will be:
     * <ul>
     * <li><code>this.result</code> if <code>this.result == that.result</code></li>
     * <li><code>that.result</code> if <code>this</code> is an empty result</li>
     * <li>{@link Status#SUCCESS} if <code>this.result != that.result</code> and <code>this.repaired + that.repaired > 0</code></li>
     * <li>{@link Status#ERROR_NOT_REPAIRED} if <code>this.result != that.result</code> and <code>this.repaired + that.repaired == 0</code></li>
     * </ul>
     * The amount of repaired items will be the sum of amounts of repaired items of the both results.
     * <p>
     * Trying to merge with an empty result will result in an {@link IllegalArgumentException}.
     *
     * @param that the result to merge with
     * @return the merged result
     */
    @NotNull
    public RepairResult merge(@NotNull RepairResult that) {
        if (that.status == null)
            throw new IllegalArgumentException("Cannot merge with an empty result!");

        int repaired = this.repaired + that.repaired;
        return new RepairResult(this.status == null ? that.status : this.status == that.status ? this.status : repaired > 0 ? Status.SUCCESS : Status.ERROR_NOT_REPAIRED, repaired);
    }

    /**
     * Initializes an empty result ({@link #getStatus()} will be <code>null</code>).
     *
     * @return an empty result
     */
    @NotNull
    public static RepairResult empty() {
        return new RepairResult(null, 0);
    }

    /**
     * Initializes a success result (with one repaired item).
     *
     * @return the success result
     */
    @NotNull
    public static RepairResult success() {
        return new RepairResult(Status.SUCCESS, 1);
    }

    /**
     * Initializes an error result (with no repaired item).
     *
     * @param status the status to initialize with
     * @return the error result
     */
    @NotNull
    public static RepairResult error(@NotNull Status status) {
        return new RepairResult(status, 0);
    }

    /**
     * Returns the status of the operation, or {@link Status#SUCCESS} if this result is empty.
     *
     * @return the status of the operation, or {@link Status#SUCCESS} if this result is empty
     */
    @NotNull
    public Status getStatus() {
        return status == null ? Status.SUCCESS : status;
    }

    /**
     * Returns the amount of items repaired.
     *
     * @return the amount of items repaired
     */
    public int getRepaired() {
        return repaired;
    }
}
