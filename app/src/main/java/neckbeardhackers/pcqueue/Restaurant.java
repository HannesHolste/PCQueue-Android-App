package neckbeardhackers.pcqueue;

import android.media.Image;

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
    private WaitTime wait;
    private Image logo;

    public static final String PARSE_CLASS = "Restaurant";
    private static final String PARSE_NAME_KEY = "Name";
    private static final String PARSE_LOCATION_KEY = "Location";
    private static final String PARSE_PHONE_KEY = "Phone";

    public Restaurant(String name) {
        this.restaurantName = name;
    }

    private Restaurant(ParseObject o) {
        this.restaurantName = o.getString(PARSE_NAME_KEY);
        this.location = o.getString(PARSE_LOCATION_KEY);
        this.phoneNumber = o.getString(PARSE_PHONE_KEY);
        this.wait = new WaitTime();
        this.wait.setCurrentWait(5);
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
    private Dictionary<String, Restaurant> restaurants;

    public RestaurantList() {
        this.restaurants = new Hashtable<String, Restaurant>();
    }

    public RestaurantList(List<Restaurant> restaurantList) {
        this.restaurants = new Hashtable<String, Restaurant>();
        for (Restaurant r : restaurantList)
            this.restaurants.put(r.getRestaurantName(), r);
    }

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<Restaurant>();
        Enumeration<Restaurant> restaurantEnumerator = this.getRestaurantsEnumerator();
        while (restaurantEnumerator.hasMoreElements()) {
            restaurantList.add(restaurantEnumerator.nextElement());
        }

        return restaurantList;
    }

    public Enumeration<Restaurant> getRestaurantsEnumerator() {
        return this.restaurants.elements();
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

    private void updateRestaurant(Restaurant restaurantToUpdate) {
        this.restaurants.put(restaurantToUpdate.getRestaurantName(), restaurantToUpdate);

    }

    private void executeGetRestaurantsQuery(final RestaurantQueryFailHandler failHandler,
                                            final RestaurantQuerySuccessHandler successHandler) {
        ParseQuery<ParseObject> restaurantQuery = ParseQuery.getQuery(Restaurant.PARSE_CLASS);
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
                Restaurant processedRestaurant = Restaurant.fromParseObject(retrievedRestaurant);
                this.updateRestaurant(processedRestaurant);
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
