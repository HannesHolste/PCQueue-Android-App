package neckbeardhackers.pcqueue;

import android.media.Image;
import android.os.AsyncTask;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
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
        this.wait.setCurrentWait(5);
        this.setHours(o.getParseObject(PARSE_HOURS_KEY));
    }

    private void setHours(ParseObject hoursParseObject) {
        if (hoursParseObject == null)
            this.hours = null;
        else {
            try {
                this.hours = OperatingHours.fromParseObject(hoursParseObject);
            }
            catch (Exception e) {
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

    public String getHoursString() {
        if (this.hours == null)
            return "Information not available";
        DailyOperatingHours todaysHours = this.hours.getHoursForToday();
        return String.format("%s-%s", todaysHours.getOpeningTime(), todaysHours.getCloseTime());
    }

    public static List<Restaurant> getSampleData() {
        List<Restaurant> restaurantList = new ArrayList<Restaurant>();

        for (int i = 0; i < 20; ++i) {
            Restaurant r = new Restaurant(String.format("Restaurant %s", i));
            r.getWaitTime().setCurrentWait(i);
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

class RestaurantList {
    private List<Restaurant> restaurants;

    public RestaurantList() {
        this.restaurants = new ArrayList<Restaurant>();
    }

    public RestaurantList(List<Restaurant> restaurantList) {
        this.restaurants = restaurantList;
    }

    public List<Restaurant> getRestaurants() {
        return this.restaurants;
    }

    public void updateRestaurantList(final RestaurantQuerySuccessHandler callback) throws Exception {
        RestaurantQueryFailHandler failHandler = new RestaurantQueryFailHandler();
        this.executeGetRestaurantsQuery(failHandler, callback);
        if (failHandler.didErrorOccur())
            throw new Exception("Error occurred when updating restaurant list");
    }

    private void retrieveAllRestaurants(final RestaurantQueryFailHandler failHandler,
                                        final RestaurantQuerySuccessHandler successHandler) {
        this.executeGetRestaurantsQuery(failHandler, successHandler);
    }

    private void addRestaurant(Restaurant restaurantToAdd) {
        this.restaurants.add(restaurantToAdd);
    }

    private void executeGetRestaurantsQuery(final RestaurantQueryFailHandler failHandler,
                                            final RestaurantQuerySuccessHandler successHandler) {
        ParseQuery<ParseObject> restaurantQuery = ParseQuery.getQuery(Restaurant.PARSE_CLASS);
        restaurantQuery.include("Hours");
        restaurantQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    handleRestaurantQuerySuccess(objects);
                    successHandler.handleRestaurantQuerySuccess();
                }
                else
                    failHandler.handleRestaurantQueryFail();
            }
        });
    }

    private void handleRestaurantQuerySuccess(List<ParseObject> retrievedRestaurants) {
        for (ParseObject retrievedRestaurant : retrievedRestaurants) {
            try {
                if (retrievedRestaurant.containsKey("Hours")) {
                }
                Restaurant processedRestaurant = Restaurant.fromParseObject(retrievedRestaurant);
                this.addRestaurant(processedRestaurant);
            }
            catch (Exception e) {
                continue;
            }
        }
    }

}

class RestaurantQuerySuccessHandler {
    public void handleRestaurantQuerySuccess() {

    }
}

class RestaurantQueryFailHandler {
    private boolean errorOccured = false;

    public void handleRestaurantQueryFail() {
        this.errorOccured = true;
    }

    public boolean didErrorOccur() {
        return this.errorOccured;
    }
}
