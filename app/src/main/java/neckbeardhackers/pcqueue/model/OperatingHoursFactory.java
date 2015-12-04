package neckbeardhackers.pcqueue.model;

import java.util.Hashtable;

/**
 * Factory pattern.
 * Creates immutable class OperatingHours
 * Description: This class is responsible for creating an object that would produce the weekly hours
 * table for each restaurant. This would create DailyOperatingHours objects for each day to hold the
 * daily operating hours.
 *
 */
public final class OperatingHoursFactory {
    Hashtable<String, DailyOperatingHours> weeklyHours = new Hashtable<>();

    /**
     * Private constructor to prevent outside instantiation.
     */
    private OperatingHoursFactory() {
    }

    /**
     * Description: Instantiation of the operating hours factory
     *
     * @return an instance of the OperatingHoursFactory
     */
    public static OperatingHoursFactory create() {
        return new OperatingHoursFactory();
    }

    /**
     * Description: This function will create the DailyOperatingHours objects through its parameters.
     *
     * @param dayName The day of the week
     * @param openTime The opening time in a string form
     * @param closeTime The closing time in string form
     * @return A modified OperatingHoursFactory that will have an additional DailyOperatingHours object
     * in its table.
     */
    public OperatingHoursFactory day(String dayName, String openTime, String closeTime) {
        // check validity of dayName name
        boolean hasValidDayName = OperatingHours.isValidDayName(dayName);

        if (!hasValidDayName) {
            throw new IllegalArgumentException("Trying to construct OperatingHours with " +
                    "invalid dayName name: " + dayName);
        }

        // store
        weeklyHours.put(convertToTitleCase(dayName), new DailyOperatingHours(dayName, openTime, closeTime));
        return this;
    }

    /**
     * Description: This function will start creating the OperatingHours object, assuming that the factory's
     * hashtable has been filled with all the DailyOperatingHours objects and covers all the days of the week.
     * Passing in the hashtable into the OperatingHours constructory, the object will then have access
     * to the hashtable created in this factory.
     *
     * @return A newly created OperatingHours object that will have all the restaurant hours for each day
     * of the week.
     */
    public OperatingHours build() {
        return new OperatingHours(weeklyHours);
    }

    /**
     * Description: A private helper function used to convert the day of the week string to a Title
     * case string.
     * @param str The string that is a day of the week
     * @return The converted day string
     */
    private String convertToTitleCase(String str) {
        StringBuilder titleCaseString = new StringBuilder();
        titleCaseString.append(Character.toTitleCase(str.charAt(0)));
        titleCaseString.append(str.substring(1).toLowerCase());
        return titleCaseString.toString();
    }

}
