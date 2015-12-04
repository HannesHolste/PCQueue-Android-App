package neckbeardhackers.pcqueue.model;

/**
 * Description: A static mapping of each "restaurant group size" to a set of minimum and upper estimates
 * for wait times in minutes.
 * We use an enum because this acts like a static lookup table; enums cannot be instantiated.
 * Minimum and maximum wait times for each group are adjacent to each other (non-overlapping),
 * so a given minute between 0 and n can always find its corresponding group.
 */
public final class WaitTimeGroup {

    public enum WAIT_TIME_GROUPS {
        LOW("0 - 5 people", 0, 10), MEDIUM("5 - 10 people", 11, 20), HIGH("10 - 20 people", 21, 40),
        VERY_HIGH("20+ people", 41, 100);

        String label;
        /**
         * Minimum wait time for this group of people, in minutes.
         */
        int clockTimeLow;

        /**
         * Maximum wait time for this group of people, in minutes.
         */
        int clockTimeHigh;

        /**
         * Description: Each wait time has a human readable string, a lower bound, and an upper
         * bound in minutes.
         *
         * @param label Human readable string.
         * @param clockTimeLow Lower bound in minutes for clock time
         * @param clockTimeHigh Upper bound in minutes for clock time
         */
        WAIT_TIME_GROUPS(String label, int clockTimeLow, int clockTimeHigh) {
            this.label = label;
            this.clockTimeLow = clockTimeLow;
            this.clockTimeHigh = clockTimeHigh;
        }

        /**
         * Description: Averages the wait time and returns it
         *
         * @return The average of the lowest and highest wait time inputs
         */
        public int getWaitTimeInMinutes() {
            return (clockTimeLow + clockTimeHigh) / 2;
        }

        /**
         * Description: Getter function to get the human readable string
         * @return String with the label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Description: Getter function to obtain the maximum wait time input
         *
         * @return The maximum wait time
         */
        public int getClockTimeHigh() {
            return clockTimeHigh;
        }

        /**
         * Description: Getter function for minimum wait time
         *
         * @return The minimum wait time
         */
        public int getClockTimeLow() {
            return clockTimeLow;
        }

        /**
         * Description: This goes through the WAIT_TIME_GROUPS enum and converts it all into
         * WaitTimeGroup objects, storing each into a WaitTimeGroups array
         *
         * @return WaitTimeGroups array with converted enum values
         */
        public static  WaitTimeGroup[] getWaitTimes() {
            WAIT_TIME_GROUPS[] enums = WAIT_TIME_GROUPS.values();
            WaitTimeGroup[] waitTimeGroups = new WaitTimeGroup[enums.length];
            for (int i = 0; i < enums.length; i++) {
                waitTimeGroups[i] = new WaitTimeGroup(enums[i]);
            }
            return waitTimeGroups;
        }
    }

    /**
     * An instance of an enum value
     */
    private WAIT_TIME_GROUPS currentWait;

    /**
     * Description: Constructor of the WaitTimeGroup object that takes in one of the enum
     * WAIT_TIME_GROUPS values
     *
     * @param wait enum value
     */
    public WaitTimeGroup(WAIT_TIME_GROUPS wait) {
        this.currentWait = wait;
    }
    
    /**
     * Description: Finds the appropriate WAIT_TIME_GROUPS object that captures the given wait time,
     * and instantiates a WaitTimeGroup object containing that group.
     *
     * We do NOT store the actual minute wait time here yet because that may change on the
     * server-side.
     *
     * @param waitTimeInMinutes Wait time in minutes.
     */
    public WaitTimeGroup(int waitTimeInMinutes) {
        if (waitTimeInMinutes < 0) {
            throw new IllegalArgumentException("Wait time in minutes cannot be negative");
        }

        WAIT_TIME_GROUPS[] enums = WAIT_TIME_GROUPS.values();

        WAIT_TIME_GROUPS group = WAIT_TIME_GROUPS.HIGH;

        // find the WAIT_TIME_GROUPS that corresponds to the given waitTimeInMinutes
        for (WAIT_TIME_GROUPS g : enums ) {
            if (waitTimeInMinutes <= g.getClockTimeHigh() && waitTimeInMinutes >= g.getClockTimeLow()) {
                group = g;
                break;
            }
        }

        this.currentWait = group;
    }

    /**
     * Description: Getter function to obtain the current wait time
     *
     * @return WAIT_TIME_GROUPS enum value set to it
     */
    public WAIT_TIME_GROUPS getCurrentWait() {
        return this.currentWait;
    }

    /**
     * Description: Obtains the label for the current wait value
     * 
     * @return A string to indicate which enum value was set to the CurrentWait object
     */
    @Override
    public String toString() {
        return currentWait.getLabel();
    }


}
