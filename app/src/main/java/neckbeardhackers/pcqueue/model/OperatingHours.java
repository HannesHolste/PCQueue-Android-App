package neckbeardhackers.pcqueue.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Immutable class OperatingHours
 * Description: This class will create the OperatingHours object, which will hold a table of
 * DailyOperatingHours objects.
 */
public final class OperatingHours {
    private final Hashtable<String, DailyOperatingHours> weeklyHours;

    /**
     * Description: The constructor to create the OperatingHours object
     * @param weeklyHours A hashtable which will hold the DailyOperatingHours objects and the corresponding
     *                    day of the week that the closing and opening times in the DailyOperatingHours
     *                    object will have.
     */
    public OperatingHours(Hashtable<String, DailyOperatingHours> weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    /**
     * Global constant, days of the week starting at Monday = index 0.
     */
    public static final String[] DAY_NAMES = {"Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};

    /**
     * Description: Getter function that returns a day of the week depending on the index given.
     *
     * @param dayNumber The appropriate index that represents a day of the week.
     * @return The string of the day of the week.
     */
    public static String getDayOfWeek(int dayNumber) {
        return DAY_NAMES[dayNumber];
    }

    /**
     * Description: A getter function that returns the hashtable with the opening and closes each day
     * of the week for a restaurant.
     *
     * @return Hashtable of DailyOperatingHours and the day of the week string as the keys.
     */
    public Dictionary<String, DailyOperatingHours> getOperatingHours() {
        return weeklyHours;
    }

    /**
     * Description: Getter function that searches through the hashtable and returns the DailyOperatingHours
     * object that tells what the current day's operating hours are.
     *
     * @param day The day of the week that the program wants to see for DailyOperatingHours
     * @return The DailyOperatingHours object for a particular day
     */
    public DailyOperatingHours getOperatingHours(String day) {
        return weeklyHours.get(day);
    }

    /**
     * Use Restaurant.isOpen() instead, which uses the server-side flag and therefore
     * supports constructing parse queries that sort by open time
     *
     * Description: This function will tell whether or not a restaurant is opened or closed from the
     * current time determined.
     * @return true The restaurant is currently open
     * @return false The restaurant is current closed
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

    /**
     * Description: Function to check if the day string given is a valid day of the week.
     * @param day The string to check if it is valid.
     * @return true The string is a valid day of the week.
     * @return false The string is an invalid day of the week.
     */
    public static boolean isValidDayName(String day) {
        for (String validDayName : OperatingHours.DAY_NAMES) {
            if (validDayName.equalsIgnoreCase(day)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Description: Function to return the string of operating hours of a restaurant
     * @return The string of operating hours for each day for a particular restaurant
     */
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

