package com.davidcubesvk.repairItem.api;

import com.davidcubesvk.repairItem.utils.Repairer;

/**
 * An API class for this plugin.
 */
public class RepairItemAPI {

    //The repairer instance
    private static Repairer repairer;

    /**
     * Returns the repairer.
     *
     * @return the repairer
     */
    public static Repairer getRepairer() {
        return repairer;
    }

    /**
     * Sets the repairer instance used by the API to the specified one. <b>This should never be used outside the plugin.</b>
     *
     * @param repairer the repairer instance
     */
    public static void setRepairer(Repairer repairer) {
        RepairItemAPI.repairer = repairer;
    }
}