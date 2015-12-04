package neckbeardhackers.pcqueue.model;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import neckbeardhackers.pcqueue.event.RestaurantChangeObserver;
import neckbeardhackers.pcqueue.event.RestaurantChangeSubject;

/**
 * Description: This singleton class will create only one RestaurantManager object that will hold a
 * list of restaurant observers. Also communicates with the Parse database to obtain updated restaurant
 * information.
 */
public class RestaurantManager implements RestaurantChangeSubject {
    private static RestaurantManager ourInstance = new RestaurantManager();
    private List<RestaurantChangeObserver> observers;

    /**
     * Description: Instantiates the list for the observers
     */
    private RestaurantManager() {
        observers = new ArrayList<>();
    }

    /**
     * Description: Returns the single instance of the RestaurantManager object.
     * @return A RestaurantManager Object
     */
    public static RestaurantManager getInstance() {
        return ourInstance;
    }


    /**
     * Description: Input for refreshing list functions.
     */
    public enum RestaurantSortType {
        NAME, WAIT_TIME
    }

    /**
     * Description: Used as a callback function for when the program calls the refresh functions.
     */
    public interface ManagerRefreshCallback {
        void handleRefreshComplete();
    }

    /**
     * Description: Function queries the database from network to obtain the updated restaurant object.
     *
     * @param id The restaurant object id in the database
     * @param local Boolean to determine whether or not to query from the local data store
     * @return ParseQuery object with the updated restaurant object
     */
    public ParseQuery<Restaurant> queryRestaurantById(String id, boolean local) {
        // Query the database from network to get the updated restaurant object
        ParseQuery<Restaurant> query = Restaurant.getQuery();
        query.whereEqualTo("objectId", id);
        if (local) {
            query.fromLocalDatastore();
        }
        return query;
    }

    /**
     * Description: Query the the local data store for the restaurant object.
     *
     * @param id The restaurant object id
     * @return ParseQuery object with restaurant information
     */
    public ParseQuery<Restaurant> queryRestaurantById(String id) {
        return queryRestaurantById(id, true);
    }


    /**
     * Description: This function will query for all the restaurants and sort them in a particular
     * order.
     *
     * @param t Sort type.
     * @param local Boolean whether or not to query from the local data store
     * @return Parse Query object
     */
    private ParseQuery<Restaurant> queryForAllRestaurants(RestaurantSortType t, boolean local) {
        ParseQuery<Restaurant> query = Restaurant.getQuery();
        if (local)
            query.fromLocalDatastore();

        if (t.equals(RestaurantSortType.NAME)) {
            // sort alphabetically by restaurant name
            query.orderByAscending("Name");
        } else if (t.equals(RestaurantSortType.WAIT_TIME)) {
            query.orderByAscending("isClosed");
            // show open restaurants first
            query.addAscendingOrder("CurrentWait");
        }
        return query;
    }

    /**
     * Description: Queries the restaurants and sorts them by name.
     *
     * @param local Boolean to determine if to query the restaurants from the local datastore or
     *              from the server.
     * @return Parse Query with sorted restaurants by name.
     */
    public ParseQuery<Restaurant> queryForAllRestaurants(boolean local) {
        return queryForAllRestaurants(RestaurantSortType.NAME, local);
    }

    /**
     * Description: Return a ParseQuery configured to query for all restaurants, sorting in the
     * specified manner.
     * This sets up the query to retrieve from local data store.
     * This will not execute the query yet!
     *
     * @param t Sort type.
     * @return Parse Query sorted by t.
     */
    public ParseQuery<Restaurant> queryForAllRestaurants(RestaurantSortType t) {
        // Configure a custom Parse query to retrieve restaurants sorted alphabetically
        return this.queryForAllRestaurants(t, true);
    }

    /**
     * Description: Obtains the ParseQuery object sorted in order by name from the local data store.
     *
     * @return Parse Query object with restaurants sorted in order by name
     */
    public ParseQuery<Restaurant> queryForAllRestaurants() {
        return queryForAllRestaurants(RestaurantSortType.NAME);
    }

    /**
     * Description: This will update the local data store.
     *
     * @param query The ParseQuery object with the restaurants' updated information.
     */
    public void executeQueryInBackground(ParseQuery<Restaurant> query) {
        executeQueryInBackground(query, null);
    }

    /**
     * Description: This function will update the local data store with the ParseQuery object passed in
     * if there isn't an error found. It also checks if a callback is provided and will execute the
     * callback as needed.
     *
     * @param query ParseQuery object to update the local data store
     * @param completeCallback The optional callback function to execute as needed
     */
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

    /**
     * Description: Function will update the local datastore by querying the server database for updated
     * restaurants' information
     */
    public void refreshAllRestaurantsHard() {
        ParseQuery<Restaurant> query = queryForAllRestaurants(false);
        executeQueryInBackground(query);
    }

    /**
     * Description: This will update local data store and execute a callback after the data store has
     * been updated.
     */
    public void refreshAllRestaurantsHard(ManagerRefreshCallback completeCallback) {
        ParseQuery<Restaurant> query = queryForAllRestaurants(false);
        executeQueryInBackground(query, completeCallback);
    }

    /**
     * Description: Updates the local datastore with the current contents of the datastore.
     *
     * @param sortType Sorting order for the restaurants
     */
    public void refreshAllRestaurantsSoft(RestaurantSortType sortType) {
        ParseQuery<Restaurant> query = queryForAllRestaurants(sortType, true);
        executeQueryInBackground(query);
    }

    /**
     * Description: Updates a particular restaurant in local datastore by querying the database.
     *
     * @param restaurantId The object id belonging to a restaurant in Parse database.
     */
    public void refreshIndividualRestaurantHard(String restaurantId) {
        // Query the database from network to get the updated restaurant object
        ParseQuery<Restaurant> query = queryRestaurantById(restaurantId, false);
        executeQueryInBackground(query);
    }

    /**
     * Description: This function removes all the local records of restaurants' information and updates
     * it with a given list of restaurant. Notifies the observers of changes in the restaurants.
     *
     * @param objects List of restaurants to replace the current local datastore
     */
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

    /**
     * Description: This function will set a restaurant's wait time.
     *
     * @param restaurantId The object id string belonging to a particular restaurant
     * @param time The number of minutes to wait in line
     */
    public void setLocalRestaurantWaitTime(String restaurantId, final int time) {
        ParseQuery<Restaurant> query = queryRestaurantById(restaurantId, true);
        query.getFirstInBackground(new GetCallback<Restaurant>() {

            @Override
            public void done(Restaurant object, ParseException e) {
                object.put("CurrentWait", time);
                object.saveInBackground();
                List<Restaurant> individualList = new ArrayList<Restaurant>();
                individualList.add(object);
                refreshParseCacheAndNotify(individualList);
            }
        });
    }

    /**
     * Description: This function will add additional observers for restaurants
     *
     * @param observer The restaurant change observer
     */
    @Override
    public synchronized void registerRestaurantChangeListener(RestaurantChangeObserver observer) {
        observers.add(observer);
    }

    /**
     * Description: Function to notify of an updated restaurant
     *
     * @param restaurant Restaurant object that has been updated.
     */
    @Override
    public void notifyObservers(Restaurant restaurant) {
        for (RestaurantChangeObserver observer : observers) {
            observer.update(restaurant);
        }
    }

    /**
     * Description: This function removes a restaurant from its list of observers
     *
     * @param observer The observer that checks to see if a restaurant has been updated.
     */
    @Override
    public synchronized void unregisterRestaurantChangeListener(RestaurantChangeObserver observer) {
        observers.remove(observer);
    }

}



