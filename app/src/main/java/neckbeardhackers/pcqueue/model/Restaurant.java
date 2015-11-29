package neckbeardhackers.pcqueue.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an Restaurant objects with its attributes.
 * Extends ParseObject to give it querying capabilities.
 */
@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {
    public String getName() {
        // These property names correspond with the Restaurant collection in Parse cloud
        return getString("Name");
    }

    /**
     * Returns Parse ID for this object
     *
     * @return String containing Parse ID for this object
     */
    public String getId() {
        return getObjectId();
    }

    public String getLocation() {
        // return location from Parse document for this Restaurant object
        return getString("Location");
    }

    public String getPhoneNumber() {
        return getString("Phone");
    }

    public String getDescription() {
        return getString("description");
    }

    public WaitTimeGroup getWaitTimeGroup() {
        return new WaitTimeGroup(getWaitInMinutes());
    }

    public int getWaitInMinutes() {
        return getInt("CurrentWait");
    }

    public boolean isClosed() {
        return (getInt("isClosed") == 1);
    }
    /**
     * Create and serialize an OperatingHours wrapper object from the raw JSON array in
     * Parse cloud, which follows the JSON object format:
     * <p>
     * [{"Monday": {"open": "9am", "close": "12am"}}, {"Tuesday": {"open": "11am", "close": "1pm"}},
     * ...until Sunday...]
     * </p>
     * For now, we do not want to cache this in a private class field because the Parse cloud
     * database value for operatingHours may change on the server side.
     *
     * @return OperatingHours object wrapper.
     */
    public OperatingHours getHours() {
        JSONArray days = getJSONArray("operatingHours");
        OperatingHoursFactory factory = OperatingHoursFactory.create();
        for (int i = 0; i < days.length(); i++) {
            try {
                // ensure  the Restaurant has opening hours defined
                // if not, it means the Restaurant is closed on this day
                if (!days.isNull(i)) {
                    JSONObject day = days.getJSONObject(i);

                    JSONObject timespanContainer = day.getJSONObject(OperatingHours.getDayOfWeek(i));
                    String startTime = timespanContainer.getString("open");
                    String endTime = timespanContainer.getString("close");
                    factory.day(OperatingHours.getDayOfWeek(i), startTime, endTime);
                }
            } catch (JSONException e) {
                System.err.println("Possibly malformed parse cloud database entry for operating hours" +
                        " for restaurant " + this);
                e.printStackTrace();
            }
        }
        return factory.build();
    }

    @Override
    public String toString() {
        return "Restaurant {id: " + getObjectId() + ", name: " + getName() + "}";
    }

    /**
     * Creates a new Parse query for the given Restaurant subclass type.
     *
     * @return A new ParseQuery
     */
    public static ParseQuery<Restaurant> getQuery() {
        return ParseQuery.getQuery(Restaurant.class);
    }

}