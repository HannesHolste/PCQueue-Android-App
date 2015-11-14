package neckbeardhackers.pcqueue.model;

import android.media.Image;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon on 10/25/15.
 * Meant to manage the data of a given restaurant
 */
public class Restaurant {
    private String restaurantName;
    private String location;
    private String phoneNumber;
    private OperatingHours hours;
    private WaitTime wait;
    private Image logo;

    public static final String PARSE_CLASS = "Restaurant";
    private static final String PARSE_NAME_KEY = "Name";
    private static final String PARSE_LOCATION_KEY = "Location";
    private static final String PARSE_PHONE_KEY = "Phone";
    private static final String PARSE_HOURS_KEY = "Hours";

    public Restaurant(String name) {
        this.restaurantName = name;
    }

    private Restaurant(ParseObject o) {
        this.restaurantName = o.getString(PARSE_NAME_KEY);
        this.location = o.getString(PARSE_LOCATION_KEY);
        this.phoneNumber = o.getString(PARSE_PHONE_KEY);
        this.wait = new WaitTime();
        this.setHours(o.getParseObject(PARSE_HOURS_KEY));
    }

    private void setHours(ParseObject hoursParseObject) {
        if (hoursParseObject == null)
            this.hours = null;
        else {
            try {
                this.hours = OperatingHours.fromParseObject(hoursParseObject);
            } catch (Exception e) {
            }
        }
    }

    public String getRestaurantName() {
        return this.restaurantName;
    }

    public WaitTime getWaitTime() {
        return this.wait;
    }

    public void setLogo(Image logo) {
        this.logo = logo;
    }


    public Image getLogo() {
        return this.logo;
    }

    public static List<Restaurant> getSampleData() {
        List<Restaurant> restaurantList = new ArrayList<Restaurant>();

        for (int i = 0; i < 20; ++i) {
            Restaurant r = new Restaurant(String.format("Restaurant %s", i));
            r.getWaitTime().setCurrentWait(WaitTime.WaitTimeByGroup.LOW);
            restaurantList.add(r);
        }

        return restaurantList;
    }

    public static Restaurant fromParseObject(ParseObject o) throws Exception {
        if (!o.getClassName().equals(Restaurant.PARSE_CLASS))
            throw new Exception("Provided ParseObject is not of Restaurant Class");

        return new Restaurant(o);
    }
}