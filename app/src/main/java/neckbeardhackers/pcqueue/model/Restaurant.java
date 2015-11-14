package neckbeardhackers.pcqueue.model;

import android.media.Image;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

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

    public WaitTime getWait() {
        // TODO
        return null;
    }

    public int getWaitInMinutes() {
        return getInt("CurrentWait");
    }

    // TODO: Hours and WaitTime

    public OperatingHours getHours() {
        // TODO
        return null;
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