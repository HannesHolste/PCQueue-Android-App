package neckbeardhackers.pcqueue;

import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by brandon on 11/1/15.
 */
public class OperatingHours {
    private Dictionary<String, DailyOperatingHours> weeklyHours;

    private static final String PARSE_KEY = "OperatingHours";
    private static final String[] DAY_KEYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday"};

    public OperatingHours() {
        this.weeklyHours = new Hashtable<String, DailyOperatingHours>();
    }

    private OperatingHours(ParseObject o) {
        this.weeklyHours = new Hashtable<String, DailyOperatingHours>();
        for (String dayKey : DAY_KEYS) {
            this.weeklyHours.put(dayKey, new DailyOperatingHours(o.getString(dayKey)));
        }
    }

    public static OperatingHours fromParseObject(ParseObject o) throws Exception {
        if (!o.getClassName().equals(PARSE_KEY))
            throw new Exception("Cannot create OperatingHours from non-OperatingHours Parse Object");

        return new OperatingHours(o);
    }

    private DailyOperatingHours getHoursForDay(String day) {
        return this.weeklyHours.get(day);
    }

    public DailyOperatingHours getHoursForToday() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return getHoursForDay(DAY_KEYS[c.get(Calendar.DAY_OF_WEEK) - 1]);
    }
}

class DailyOperatingHours {
    private boolean open;
    private String openTime;
    private String closeTime;

    private static final String CLOSED_KEY = "CLOSED";

    public DailyOperatingHours(String openTime, String closeTime) {
        this.open = true;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public DailyOperatingHours(String jointTimes) {
        if (jointTimes.equals(CLOSED_KEY)) {
            this.open = false;
        }
        else {
            this.open = true;
            String[] splitTimes = jointTimes.split("-");
            this.openTime = getStandardizedTimeString(splitTimes[0]);
            this.closeTime = getStandardizedTimeString(splitTimes[1]);
        }
    }

    public String getOpeningTime() {
        return this.openTime;
    }

    public String getCloseTime() {
        return this.closeTime;
    }

    private String getStandardizedTimeString(String timeString) {
        if (!timeString.contains(":")) {
            timeString += ":00";
        }

        String[] sectionedTimeString = timeString.split(":");
        int hourChunk = Integer.parseInt(sectionedTimeString[0]);
        if (hourChunk > 12) {
            hourChunk -= 12;
            sectionedTimeString[1] += " PM";
        }
        else {
            if (hourChunk == 0)
                hourChunk = 12;
            sectionedTimeString[1] += " AM";
        }

        return String.format("%s:%s", Integer.toString(hourChunk), sectionedTimeString[1]);
    }
}
