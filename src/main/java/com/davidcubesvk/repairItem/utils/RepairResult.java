package com.davidcubesvk.repairItem.utils;

/**
 * A result of a repair operation.
 */
public class RepairResult {

    /**
     * Status of the operation.
     * <ul>
     *     <li><code>SUCCESS</code>: succeeded (at least one item was repaired)</li>
     *     <li><code>FAIL_GENERALIZED</code>: failed because of an error (generalized message)</li>
     *     <li><code>FAIL_NO_ITEM</code>: failed because no item was found</li>
     *     <li><code>FAIL_UNREPAIRABLE</code>: failed because no repairable item was found</li>
     *     <li><code>FAIL_BLOCKED</code>: failed because all items are blocked</li>
     *     <li><code>FAIL_ALREADY_REPAIRED</code>: failed because all items are already repaired</li>
     *     <li><code>FAIL_UNSUPPORTED</code>: failed because the operation is not supported</li>
     *     <li><code>FAIL_OTHER</code>: failed because of other error</li>
     * </ul>
     */
    public enum Status {
        SUCCESS(null), FAIL_GENERALIZED("generalized"), FAIL_NO_ITEM("specific.no-repairable-item"),
        FAIL_UNREPAIRABLE("specific.no-repairable-item"), FAIL_BLOCKED("specific.no-repairable-item"),
        FAIL_ALREADY_REPAIRED("specific.no-repairable-item"), FAIL_UNSUPPORTED("specific.unsupported"),
        FAIL_OTHER("specific.other");

        //The path
        private String path;

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
    private Status status;
    //Amount of items repaired
    private byte repaired;

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
