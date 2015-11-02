package neckbeardhackers.pcqueue;

import com.parse.ParseObject;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by brandon on 11/1/15.
 */
public class OperatingHours {
    private Dictionary<String, DailyOperatingHours> weeklyHours;

    private static final String PARSE_KEY = "OperatingHours";

    public OperatingHours() {
        this.weeklyHours = new Hashtable<String, DailyOperatingHours>();
    }

    private OperatingHours(ParseObject o) {
    }

    public static OperatingHours fromParseObject(ParseObject o) throws Exception {
        if (!o.getClassName().equals(PARSE_KEY))
            throw new Exception("Cannot create OperatingHours from non-OperatingHours Parse Object");

        return new OperatingHours(o);
    }

    private DailyOperatingHours getHoursForDay(String day) {

    }

}

class DailyOperatingHours {
    private String openTime;
    private String closeTime;

    public DailyOperatingHours(String openTime, String closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public DailyOperatingHours(String jointTimes) {
        String[] splitTimes = jointTimes.split("-");
        this.openTime = getStandardizedTimeString(splitTimes[0]);
        this.closeTime = getStandardizedTimeString(splitTimes[1]);
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
        int hourChunk = Integer.parseInt(sectionTimeString[0]);
        if (hourChunk > 12) {
            hourChunk -= 12;
            sectionedTimeString[1] += " PM";
        }
        else
            sectionedTimeString[1] += " AM";

        return String.format("%s:%s", sectionedTimeString[0], sectionedTimeString[1]);
    }
}
