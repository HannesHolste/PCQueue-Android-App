package neckbeardhackers.pcqueue.model;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hannes on 11/13/15.
 */
public class RestaurantList {
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

  

}
