package neckbeardhackers.pcqueue.model;

import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Immutable class OperatingHours
 */
public final class OperatingHours {
    private final Dictionary<String, DailyOperatingHours> weeklyHours;

    /**
     * Global constant, days of the week starting at Monday = index 0.
     */
    public static final String[] DAY_NAMES = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public OperatingHours(Dictionary<String, DailyOperatingHours> weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public Dictionary<String, DailyOperatingHours> getOperatingHours() {
        return weeklyHours;
    }

    public DailyOperatingHours getOperatingHours(String day) {
        return weeklyHours.get(day);
    }

    public boolean isOpenNow() throws Exception {
        // TODO
        throw new Exception("Needs implementation");
        // TODO:
        // Find current day. Lookup day in weeklyHours to find corresponding DailyOperatingHours
        // Call its isOpenNow() method and return the result
    }
}

/**
 * Factory pattern.
 * Creates immutable class OperatingHours
 */
final class OperatingHoursFactory {
    Dictionary<String, DailyOperatingHours> weeklyHours = new Hashtable<>();

    /**
     * Private constructor to prevent outside instantiation.
     */
    private OperatingHoursFactory() {
    }

    public static OperatingHoursFactory create() {
        return new OperatingHoursFactory();
    }

    public OperatingHoursFactory day(String dayName, String openTime, String closeTime) {
        boolean hasValidDayName = false;
        // check validity of day name
        for (String validDayName : OperatingHours.DAY_NAMES) {
            if (dayName.equalsIgnoreCase(validDayName)) {
                hasValidDayName = true;
                break;
            }
        }

        if (!hasValidDayName) {
            throw new IllegalArgumentException("Trying to construct OperatingHours with invalid day name: " + dayName);
        }

        // check validity of start and end time
        // TODO

        // store
        weeklyHours.put(dayName, new DailyOperatingHours(openTime, closeTime));
        return this;
    }

    public OperatingHours build() {
        return new OperatingHours(weeklyHours);
    }

}

final class DailyOperatingHours {
    private final String openTime;
    private final String closeTime;

    public DailyOperatingHours(String openTime, String closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public String getOpeningTimeString() {
        return this.openTime;
    }

    public String getCloseTimeString() {
        return this.closeTime;
    }

    public boolean isOpenNow() throws Exception {
        // TODO
        // Interpret String open time and close time
        // Get current system time
        // check if system time is in the interval (open, close)
        throw new Exception("Needs implementation");
    }

    @Override
    public String toString() {
        // TODO pre-process openTime and closeTime to standardize the date format
        return openTime + " - " + closeTime;
    }
}
