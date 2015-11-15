package neckbeardhackers.pcqueue.model;

import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import neckbeardhackers.pcqueue.event.RestaurantChangeObserver;
import neckbeardhackers.pcqueue.event.RestaurantChangeSubject;

/**
 * singleton class
 */
public class RestaurantManager implements RestaurantChangeSubject {
    private static RestaurantManager ourInstance = new RestaurantManager();
    private List<RestaurantChangeObserver> observers;

    public enum RestaurantSortType {
        NAME, WAIT_TIME
    }

    public static RestaurantManager getInstance() {
        return ourInstance;
    }

    private RestaurantManager() {
        observers = new ArrayList<>();
    }

    public ParseQuery<Restaurant> queryRestaurantById(String id, boolean local) {
        // Query the database from network to get the updated restaurant object
        ParseQuery<Restaurant> query = Restaurant.getQuery();
        query.whereEqualTo("objectId", id);
        if (local) {
            query.fromLocalDatastore();
        }
        return query;
    }

    public ParseQuery<Restaurant> queryRestaurantById(String id) {
        return queryRestaurantById(id, true);
    }

    /**
     * Return a ParseQuery configured to query for all restaurants, sorting in the specified manner.
     * This sets up the query to retrieve from local data store.
     * This will not execute the query yet!
     * @param t Sort type.
     * @return Parse Query
     */
    public ParseQuery<Restaurant> queryForAllRestaurants(RestaurantSortType t) {
        // Configure a custom Parse query to retrieve restaurants sorted alphabetically
        ParseQuery<Restaurant> query = Restaurant.getQuery();
        query.fromLocalDatastore();

        if (t.equals(RestaurantSortType.NAME)) {
            // sort alphabetically by restaurant name
            query.orderByAscending("Name");
        } else if (t.equals(RestaurantSortType.WAIT_TIME)) {
            query.orderByAscending("WaitTime");
        }
        return query;
    }

    public ParseQuery<Restaurant> queryForAllRestaurants() {
        return queryForAllRestaurants(RestaurantSortType.NAME);
    }

    @Override
    public synchronized void registerRestaurantChangeListener(RestaurantChangeObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(Restaurant restaurant) {
        for (RestaurantChangeObserver observer : observers) {
            observer.update(restaurant);
        }
    }
}
