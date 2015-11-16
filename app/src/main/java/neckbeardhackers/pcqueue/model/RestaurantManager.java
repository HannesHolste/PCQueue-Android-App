package neckbeardhackers.pcqueue.model;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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

    public interface ManagerRefreshCallback {
        void handleRefreshComplete();
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
        return this.queryForAllRestaurants(t, true);
    }

    private ParseQuery<Restaurant> queryForAllRestaurants(RestaurantSortType t, boolean local) {
        ParseQuery<Restaurant> query = Restaurant.getQuery();
        if (local)
            query.fromLocalDatastore();

        if (t.equals(RestaurantSortType.NAME)) {
            // sort alphabetically by restaurant name
            query.orderByAscending("Name");
        } else if (t.equals(RestaurantSortType.WAIT_TIME)) {
            query.orderByAscending("WaitTime");
        }
        return query;
    }

    public void refreshAllRestaurantsHard() {
        ParseQuery<Restaurant> query = queryForAllRestaurants(false);
        executeQueryInBackground(query);
    }

    public void refreshAllRestaurantsHard(ManagerRefreshCallback completeCallback) {
        ParseQuery<Restaurant> query = queryForAllRestaurants(false);
        executeQueryInBackground(query, completeCallback);
    }

    public void executeQueryInBackground(ParseQuery<Restaurant> query) {
        executeQueryInBackground(query, null);
    }

    public void executeQueryInBackground(ParseQuery<Restaurant> query, final ManagerRefreshCallback completeCallback) {
        query.findInBackground(new FindCallback<Restaurant>() {

        @Override
        public void done(final List<Restaurant> objects, ParseException e) {
            if (e == null)
                refreshParseCacheAndNotify(objects);
                if (completeCallback != null)
                    completeCallback.handleRefreshComplete();
            }
        });
    }


    public void refreshParseCacheAndNotify(final List<Restaurant> objects) {
        ParseObject.unpinAllInBackground(objects, new DeleteCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(objects);
                    for (Restaurant restaurant : objects)
                        notifyObservers(restaurant);
                }
            }
        });
    }

    public void refreshIndividualRestaurantHard(String restaurantId) {
        // Query the database from network to get the updated restaurant object
        ParseQuery<Restaurant> query = queryRestaurantById(restaurantId, false);
        executeQueryInBackground(query);
    }

    public ParseQuery<Restaurant> queryForAllRestaurants() {
        return queryForAllRestaurants(RestaurantSortType.NAME);
    }

    public ParseQuery<Restaurant> queryForAllRestaurants(boolean local) {
        return queryForAllRestaurants(RestaurantSortType.NAME, local);
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



