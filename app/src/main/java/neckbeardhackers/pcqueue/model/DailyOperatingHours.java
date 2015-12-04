package neckbeardhackers.pcqueue.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class name: DailyOperatingHours
 * Purpose: This class is responsible for creating the DailyOperatingHours object which will hold the
 * opening and closed times of a particular restaurant on a given day. Multiple of these objects will
 * be created for each restaurant with each having their own opening and closing time
 */
public final class DailyOperatingHours {
    private final String dayName;
    private final String openTime;
    private final String closeTime;
    private static final String DEFAULT_TIME = "12:00am";

    /**
     * Description: The constructor to create a DailyOperatingHours object for a restaurant for a
     * particular day. Each day will have a DailyOperatingHours object which will have the open and
     * close time, along with the day of the week.
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

    /**
     * Description: Getter function to return the open time string of the DailyOperatingHours object
     * @return The openTime string
     */
    public String getOpeningTimeString() {
        return this.openTime;
    }

    /**
     * Description: Getter function to obtain the closed time string of the DailyOperatingHours object
     * @return The closeTime string
     */
    public String getCloseTimeString() {
        return this.closeTime;
    }

    /**
     * Description: Getter function that returns the day of the DailyOperatingHour object
     * @return The string of the day of the week
     */
    public String getDayString() {
        return dayName;
    }

    /**
     * Using Restaurant.isOpen() instead, which on the server side and supports sorting parse objects
     * by opening time also.
     *
     * Description: This function will report whether or not the current time is within the range of
     * the open and close time of the DailyOperatingHours object.
     * @return true The current time is within the range, and therefore, is open
     * @return false The current time is outside the range, then it is closed
     */
    @Deprecated
    public boolean isOpenNow() {
        //Creating Calendar objects from openTime and closeTime strings obtained from parse

        if(closedAllDay()) return false;

        Calendar currentTime = Calendar.getInstance();
        Calendar open = getCalendarFromTimeString(openTime, false, currentTime);
        Calendar close = getCalendarFromTimeString(closeTime, true, currentTime);

        //Checking if close is equal to open, if so, the restaurant is 24 hours
        if(close.compareTo(open) == 0) return true;

        //Checking if the current time is within the bounds of the open and closing time
        return timeIsWithinHours(currentTime, open, close);
    }

    /**
     * Description: This is a helper function that converts the open and close time strings into
     * the appropriate calendar object
     * @param time The open or close time string
     * @param shiftIfAfterMidnight To determine if there needs to be a shift in the day to ensure it
     *                             can check for midnight
     * @param baseCal The current day's calendar
     * @return A new calendar object that will have its time set to whatever open or close time is
     */
    private static Calendar getCalendarFromTimeString(String time, boolean shiftIfAfterMidnight,
                                                      Calendar baseCal) {
        DateFormat parseTimeFormat = new SimpleDateFormat("hh:mmaa");
        Calendar appliedCalendar = Calendar.getInstance();
        //sets the appliedCalendar's time format to parseTimeFormat and parses the given
        // time string
        try {
            appliedCalendar.setTime(parseTimeFormat.parse(time));
            appliedCalendar.set(baseCal.get(Calendar.YEAR), baseCal.get(Calendar.MONTH),
                    baseCal.get(Calendar.DAY_OF_MONTH));

            // If shift is selected, then we want to advance the day by one day (Happens when
            // a restaurant closes at sometime in the morning (e.g 1am)
            if (shiftIfAfterMidnight && appliedCalendar.get(Calendar.HOUR_OF_DAY) < 12)
                appliedCalendar.add(Calendar.DAY_OF_MONTH, 1);

        }catch (Exception e){
            // After the time is verified in the above TODO, this should never occur.
            System.err.print("Given time is not the appropriate format");
        }

        return appliedCalendar;
    }

    /**
     * Description: A helper function to check if the current time is within the open and close time
     * calendar objects
     *
     * @param currentTime Current time calendar
     * @param openTime Calendar with open time set to it
     * @param closeTime Calendar with close time set to it
     * @return
     */
    private static boolean timeIsWithinHours(Calendar currentTime, Calendar openTime,
                                             Calendar closeTime){
        return (currentTime.compareTo(openTime) >=0 && currentTime.compareTo(closeTime) <=0);
    }

    @Override
    /**
     * Description: This function will return the appropriate string to indicate the the DailyOperatingHour
     * times, if its closed, or it is open 24 hours.
     *
     * @return The string that will tell what the DailyOperatingHours object's current hours on a day
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dayName);
        s.append(": ");

        if (openTime == "null" || closeTime == "null") {
            s.append("closed");
        } else if (openTime.equals("12:00am") && closeTime.equals("11:59pm")) {
            s.append("24 hours");
        } else {
            s.append(openTime);
            s.append(" - ");
            s.append(closeTime);
        }
        return s.toString();
    }

    /**
     * Description: This function will check if DailyOperatingHours for a particular day is closed.
     *
     * @return true DailyOperatingHours for that day is closed all day
     * @return false DailyOperatingHours is open
     */
    public boolean closedAllDay(){
        if(openTime == "null" && closeTime == "null"){
            return true;
        }
        return false;
    }

    /**
     * Description: This will check if the DailyOperatingHours indicates that the restaurant does not
     * close for a particular day.
     *
     * @return true DailyOperatingHours is open all day and night
     * @return false DailyOperatingHours have two different open and close times
     */
    public boolean doesNotClose(){
        if(openTime.equals("12:00am") && closeTime.equals("11:59pm")){
            return true;
        }
        return false;
    }
}
