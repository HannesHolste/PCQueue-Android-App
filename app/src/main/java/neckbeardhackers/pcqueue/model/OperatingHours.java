package neckbeardhackers.pcqueue.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Immutable class OperatingHours
 */
public final class OperatingHours {
    private final Hashtable<String, DailyOperatingHours> weeklyHours;

    /**
     * Global constant, days of the week starting at Monday = index 0.
     */
    public static final String[] DAY_NAMES = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday"};

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
        //throw new Exception("Needs implementation");
        // TODO:
        // Find current dayName. Lookup dayName in weeklyHours to find corresponding DailyOperatingHours
        // if weeklyHours.get(dayName) returns null, that means the restaurant is closed on that dayName.
        // simply return false. Else:
        // Call its isOpenNow() method and return that result

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(weeklyHours.get(DAY_NAMES[day-1]) == null) return false;

        return weeklyHours.get(DAY_NAMES[day-1]).isOpenNow();

    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Hours: ");
        for (DailyOperatingHours d : weeklyHours.values()) {
            s.append(d);
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
        boolean hasValidDayName = false;
        // check validity of dayName name
        for (String validDayName : OperatingHours.DAY_NAMES) {
            if (dayName.equalsIgnoreCase(validDayName)) {
                hasValidDayName = true;
                break;
            }
        }

        if (!hasValidDayName) {
            throw new IllegalArgumentException("Trying to construct OperatingHours with invalid dayName name: " + dayName);
        }

        // check validity of start and end time
        // TODO

        // store
        weeklyHours.put(dayName, new DailyOperatingHours(dayName, openTime, closeTime));
        return this;
    }

    public OperatingHours build() {
        return new OperatingHours(weeklyHours);
    }

}

final class DailyOperatingHours {
    private final String dayName;
    private final String openTime;
    private final String closeTime;

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
        this(day, null, null);
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

        DateFormat timeFormat = new SimpleDateFormat("hh:mmaa");
        Calendar open = Calendar.getInstance();
        Calendar close = Calendar.getInstance();
        Calendar time = Calendar.getInstance();
        open.setTime(timeFormat.parse(openTime));
        close.setTime(timeFormat.parse(closeTime));
        if(close.compareTo(open) == 0) return true;
        if(time.compareTo(open) > 0 && time.compareTo(close) < 0){
            return true;
        }
        return false;
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
