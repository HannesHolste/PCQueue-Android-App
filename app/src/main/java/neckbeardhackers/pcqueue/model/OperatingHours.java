package neckbeardhackers.pcqueue.model;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Immutable class OperatingHours
 */
public final class OperatingHours {
    private final Hashtable<String, DailyOperatingHours> weeklyHours;

    /**
     * Global constant, days of the week starting at Monday = index 0.
     */
    public static final String[] DAY_NAMES = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
    "Saturday", "Sunday"};

    public static String getDayOfWeek(int dayNumber) {
        return DAY_NAMES[dayNumber];
    }

    public OperatingHours(Hashtable<String, DailyOperatingHours> weeklyHours) {
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
        // Find current dayName. Lookup dayName in weeklyHours to find corresponding DailyOperatingHours
        // if weeklyHours.get(dayName) returns null, that means the restaurant is closed on that dayName.
        // simply return false. Else:
        // Call its isOpenNow() method and return that result
    }

    public static boolean isValidDayName(String day) {
        for (String validDayName : OperatingHours.DAY_NAMES) {
            if (validDayName.equalsIgnoreCase(day)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Hours: ");
        for (DailyOperatingHours d : weeklyHours.values()) {
            s.append(d.toString());
            s.append("\n");
        }

        return s.toString();
    }
}

/**
 * Factory pattern.
 * Creates immutable class OperatingHours
 */
final class OperatingHoursFactory {
    Hashtable<String, DailyOperatingHours> weeklyHours = new Hashtable<>();

    /**
     * Private constructor to prevent outside instantiation.
     */
    private OperatingHoursFactory() {
    }

    public static OperatingHoursFactory create() {
        return new OperatingHoursFactory();
    }

    public OperatingHoursFactory day(String dayName, String openTime, String closeTime) {
        // check validity of dayName name
        boolean hasValidDayName = OperatingHours.isValidDayName(dayName);

        if (!hasValidDayName)
            throw new IllegalArgumentException("Trying to construct OperatingHours with invalid dayName name: " + dayName);

        // check validity of start and end time
        // TODO

        // store
        weeklyHours.put(convertToTitleCase(dayName), new DailyOperatingHours(dayName, openTime, closeTime));
        return this;
    }

    public OperatingHours build() {
        return new OperatingHours(weeklyHours);
    }

    private String convertToTitleCase(String str) {
        StringBuilder titleCaseString = new StringBuilder();
        titleCaseString.append(Character.toTitleCase(str.charAt(0)));
        titleCaseString.append(str.substring(1).toLowerCase());
        return titleCaseString.toString();
    }

}

final class DailyOperatingHours {
    private final String dayName;
    private final String openTime;
    private final String closeTime;
    private static final String DEFAULT_TIME = "12:00am";

    /**
     *
     * @param day Day of week as a string, e.g. "Monday"
     * @param openTime open time as a string
     * @param closeTime open time as a string
     */
    public DailyOperatingHours(String day, String openTime, String closeTime) {
        this.dayName = day;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    // Call this if you want to denote "open 24 hours per day"
    public DailyOperatingHours(String day) {
        this(day, DEFAULT_TIME, DEFAULT_TIME);
    }

    public static DailyOperatingHours getClosedInstance(String day) {
        return new DailyOperatingHours(day, null, null);
    }

    public String getOpeningTimeString() {
        return this.openTime;
    }

    public String getCloseTimeString() {
        return this.closeTime;
    }

    public String getDayString() {
        return dayName;
    }

    public boolean isOpenNow() throws Exception {
        // TODO
        // Interpret String openTime and closeTime
        // if openTime == closeTime, then the restaurant is open 24 hours this dayName.
        // Simply return true.
        // Get current system time
        // check if system time is in the interval (openTime, closeTime)

        throw new Exception("Needs implementation");
    }

    @Override
    public String toString() {
        // TODO pre-process openTime and closeTime to standardize the date format
        StringBuilder s = new StringBuilder();
        s.append(dayName);
        s.append(": ");

        if (openTime == null || closeTime == null) {
            s.append("closed");
        } else if (openTime.equalsIgnoreCase(closeTime)) {
            s.append("24 hours");
        } else {
            s.append(openTime);
            s.append(" - ");
            s.append(closeTime);
        }
        return s.toString();
    }
}
