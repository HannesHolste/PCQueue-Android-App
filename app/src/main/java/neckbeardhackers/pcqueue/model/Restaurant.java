package neckbeardhackers.pcqueue.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class:Restaurant
 * Description: Represents an Restaurant objects with its attributes.
 * Extends ParseObject to give it querying capabilities.
 */
@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {
    public String getName() {
        // These property names correspond with the Restaurant collection in Parse cloud
        return getString("Name");
    }

    /**
     * Description: Returns Parse ID for this object
     *
     * @return String containing Parse ID for this object
     */
    public String getId() {
        return getObjectId();
    }

    /**
     * Description: Getter function for obtaining the location from the db
     *
     * @return A string that is the address of the restaurant
     */
    public String getLocation() {
        // return location from Parse document for this Restaurant object
        return getString("Location");
    }

    /**
     * Description: Getter function for obtaining the phone number from the db
     *
     * @return A string that is the phone number of the restaurant
     */
    public String getPhoneNumber() {
        return getString("Phone");
    }

    /**
     * Description: Getter function for obtaining the restaurant description from the db
     *
     * @return A string that is the general description of the restaurant
     */
    public String getDescription() {
        return getString("description");
    }

    /**
     * Description: This function instantiates a new wait time group, which will hold all of the
     * wait times for restaurants
     *
     * @return A new waittimegroup object with current restaurant wait times
     */
    public WaitTimeGroup getWaitTimeGroup() {
        return new WaitTimeGroup(getWaitInMinutes());
    }

    /**
     * Description: Getter function for obtaining the current wait time for each restaurant
     *
     * @return A string that shows what the current predicted wait time is
     */
    public int getWaitInMinutes() {
        return getInt("CurrentWait");
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

    /**
     *
     * The reason isClosed is an int, not a boolean, in the parse db is because
     * And the reason why we use isClosed server-side is because Parse doesn't
     * support ascending and descending ordering in the same query and sorting doesn't work
     * on booleans at all
     *
     * Description: A function that checks if the restaurant is closed or not based on the report
     * from parse
     *
     * @return true The restaurant is open
     * @return false The restaurant is closed
     */
    public boolean isOpenNow() {
        return getInt("isClosed") == 0;
    }

    @Override
    public String toString() {
        return "Restaurant {id: " + getObjectId() + ", name: " + getName() + ", waitTime: " + getWaitInMinutes() + "," +
                "isOpen: " + isOpenNow() + "}";
    }

    /**
     * Description: Creates a new Parse query for the given Restaurant subclass type.
     *
     * @return A new ParseQuery
     */
    public static ParseQuery<Restaurant> getQuery() {
        return ParseQuery.getQuery(Restaurant.class);
    }

    @Override
    /**
     * Description: Function to check if another restaurant shares its id
     *
     * @return true The restaurant has a duplicate
     * @return false The restaurant is unique in the database
     */
    public boolean equals(Object o) {
        if (!(o instanceof Restaurant))
            return false;

        return ((Restaurant)o).getId().equals(this.getId());
    }

    /**
     * Description: Function to chek if another restaurant has differences in name and wait time
     *
     * @param o The object to compare with the current restaurant
     * @return true The restaurant names are not equal or the wait time is not the same, or the object
     * is not a restaurant
     * @return false The restaurant has differences in wait time or name, and o is an instance of
     * restaurant
     */
    public boolean hasDifferences(Object o) {
        if (!(o instanceof Restaurant))
            return true;

        Restaurant toCompare = (Restaurant)o;
        if (!(toCompare.getName().equals(this.getName())))
            return true;
        if (toCompare.getWaitInMinutes() != this.getWaitInMinutes())
            return true;

        return false;
    }

}