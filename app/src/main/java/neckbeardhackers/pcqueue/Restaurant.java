package neckbeardhackers.pcqueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon on 10/25/15.
 * Meant to manage the data of a given restaurant
 */
public class Restaurant {
    private String restaurantName;
    private WaitTime wait;
    //private Image logo;

    public Restaurant(String name) {
        this.restaurantName = name;
        this.wait = new WaitTime();
    }

    public String getRestaurantName() {
        return this.restaurantName;
    }

    public WaitTime getWaitTime() {
        return this.wait;
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

}
