package neckbeardhackers.pcqueue.model;

import java.util.Hashtable;

/**
 * Factory pattern.
 * Creates immutable class OperatingHours
 */
public final class OperatingHoursFactory {
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

        if (!hasValidDayName) {
            throw new IllegalArgumentException("Trying to construct OperatingHours with " +
                    "invalid dayName name: " + dayName);
        }

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
