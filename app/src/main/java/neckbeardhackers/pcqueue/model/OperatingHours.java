package neckbeardhackers.pcqueue.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public static final String[] DAY_NAMES = {"Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};

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

    /**
     * Use Restaurant.isOpen() instead, which uses the server-side flag and therefore
     * supports constructing parse queries that sort by open time
     * @return
     */
    @Deprecated
    public boolean isOpenNow(){

        //Get the current day
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //Shift the day to correctly correspond with the appropriate DAY_NAME
        //since java.util.Calendar returns 1 for Sunday and 7 for Saturday
        int actualDay = day-1;
        //if the day returns null, it means that the restaurant is closed
        if(weeklyHours.get(DAY_NAMES[actualDay]) == null) return false;
        //check if the restaurant is open given the day, which will check the hours
        return weeklyHours.get(DAY_NAMES[actualDay]).isOpenNow();

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

