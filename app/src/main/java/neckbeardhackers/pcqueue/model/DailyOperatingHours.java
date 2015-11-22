package neckbeardhackers.pcqueue.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class DailyOperatingHours {
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

    public boolean isClosed(){
        if(openTime == "null" && closeTime == "null"){
            return true;
        }
        return false;
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

    public boolean isOpenNow() {
        //Creating Calendar objects from openTime and closeTime strings obtained from parse
        //If open and close time are null, then it means the restaurant is closed
        if(openTime == "null" && closeTime == "null") return false;
        Calendar currentTime = Calendar.getInstance();
        Calendar open = getCalendarFromTimeString(openTime, false, currentTime);
        Calendar close = getCalendarFromTimeString(closeTime, true, currentTime);
        //Checking if close is equal to open, if so, the restaurant is 24 hours
        if(close.compareTo(open) == 0) return true;

        //Checking if the current time is within the bounds of the open and closing time
        return timeIsWithinHours(currentTime, open, close);
    }

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

    private static boolean timeIsWithinHours(Calendar currentTime, Calendar openTime,
                                             Calendar closeTime){

        System.out.println("The current closing hour is: " + closeTime.get(Calendar.HOUR_OF_DAY));
        return (currentTime.compareTo(openTime) >=0 && currentTime.compareTo(closeTime) <=0);
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
