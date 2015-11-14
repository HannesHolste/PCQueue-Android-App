package neckbeardhackers.pcqueue.model;

/**
 * Created by brandon on 10/25/15.
 * Meant to easily keep track of wait times
 */
public class WaitTime {

    public enum WaitTimeByGroup {
        LOW("0 - 5 people", 0, 10), MEDIUM("5 - 10 people", 11, 20), HIGH("10 - 20 people", 21, 40),
        VERY_HIGH("20+ people", 41, 100);

        String label;
        int clockTimeLow;
        int clockTimeHigh;

        /**
         * Each wait time has a human readable string, a lower bound, and an upper bound in minutes.
         *
         * @param label Human readable string.
         * @param clockTimeLow Lower bound in minutes for clock time
         * @param clockTimeHigh Upper bound in minutes for clock time
         */
        WaitTimeByGroup(String label, int clockTimeLow, int clockTimeHigh) {
            this.label = label;
            this.clockTimeLow = clockTimeLow;
            this.clockTimeHigh = clockTimeHigh;
        }

        public int getWaitTimeInMinutes() {
            return (clockTimeLow + clockTimeHigh) / 2;
        }

        public String getLabel() {
            return label;
        }

        public int getClockTimeHigh() {
            return clockTimeHigh;
        }

        public int getClockTimeLow() {
            return clockTimeLow;
        }

        public static  WaitTime[] getWaitTimes() {
            WaitTimeByGroup[] enums = WaitTimeByGroup.values();
            WaitTime[] waitTimes = new WaitTime[enums.length];
            for (int i = 0; i < enums.length; i++) {
                waitTimes[i] = new WaitTime(enums[i]);
            }
            return waitTimes;
        }
    }

    private WaitTimeByGroup currentWait;

    public WaitTime() {
        this(WaitTimeByGroup.LOW);
    }

    public WaitTime(WaitTimeByGroup wait) {
        this.currentWait = wait;
    }

    public WaitTime(int waitTimeInMinutes) {
        if (waitTimeInMinutes < 0) {
            throw new IllegalArgumentException("Wait time in minutes cannot be negative");
        }

        WaitTimeByGroup[] enums = WaitTimeByGroup.values();

        WaitTimeByGroup group = WaitTimeByGroup.HIGH;

        // find the WaitTimeByGroup that corresponds to the given waitTimeInMinutes
        for (WaitTimeByGroup g : enums ) {
            if (waitTimeInMinutes <= g.getClockTimeHigh() && waitTimeInMinutes >= g.getClockTimeLow()) {
                group = g;
                break;
            }
        }

        this.currentWait = group;
    }

    public WaitTimeByGroup getCurrentWait() {
        return this.currentWait;
    }

    public void setCurrentWait(WaitTimeByGroup w) {
        this.currentWait = w;
    }

    @Override
    public String toString() {
        return currentWait.getLabel();
    }


}
