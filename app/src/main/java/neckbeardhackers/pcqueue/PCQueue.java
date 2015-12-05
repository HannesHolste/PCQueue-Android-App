package neckbeardhackers.pcqueue;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import neckbeardhackers.pcqueue.event.NetworkConnectionChangeObserver;
import neckbeardhackers.pcqueue.model.Restaurant;
import neckbeardhackers.pcqueue.net.NetworkStateReceiver;

/**
 * Main entry point for the application. Contains logic for registering the the app with parse for
 * updates and differentiating between test and prod Parse databases.
 */
public class PCQueue extends Application {

    // TODO: Extract this to a separate configuration file, and re-generate keys
    // Leaving keys in source code that is committed to github is poor practice
    private final String PROD_PARSE_APP_ID = "***REMOVED***";
    private final String PROD_PARSE_CLIENT_KEY = "***REMOVED***";
    private final String TEST_PARSE_APP_ID = "***REMOVED***";
    private final String TEST_PARSE_CLIENT_KEY = "***REMOVED***";

    /**
     * Determines whether or not the application is being run as an app or for testing purposes by
     * checking to see if the Test classes are in scope. Janky implementation.
     * @return Whether we are running tests
     */
    private static boolean isTestMode() {
        boolean result;
        try {
            Class.forName("neckbeardhackers.pcqueue.RestaurantListTest");
            result = true;
        } catch (final Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * Handles the Application onCreate method by registering the App with the parse database and
     * subscribing to notification channels. This is called when the application starts.
     */
    @Override
    public void onCreate() {
        String parseAppId = PROD_PARSE_APP_ID;
        String parseClientKey = PROD_PARSE_CLIENT_KEY;
        if (isTestMode()) {
            System.err.println("TESTING!");
            parseAppId = TEST_PARSE_APP_ID;
            parseClientKey = TEST_PARSE_CLIENT_KEY;
        }
        super.onCreate();

        // Enable storing data locally first. We'll take care of syncing it.
        Parse.enableLocalDatastore(getApplicationContext());

        // Register Parse Object subclasses that we use to represent our data model
        // and that are kept in sync with Parse
        ParseObject.registerSubclass(Restaurant.class);

        // Initialize Parse client with authentication information
        Parse.initialize(this, parseAppId, parseClientKey);


        // Enable anonymous users, so we can save data to the local data store without logging in
        // We shall never log them out and anonymoous users can sync with Parse cloud
        ParseUser.enableAutomaticUser();

        // Setup default Access Control List (ACL), related to write/read permissions
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true /* give access to current user */);

        // Pin all Restaurant objects so they're accessible from the local data store
        // Pinning means caching the network-retrieved restaurant objects into local data store
        ParseQuery<Restaurant> query = Restaurant.getQuery();
        try {
            // TODO: Show a loading app indicator in the UI while this is happening
            List<Restaurant> list = query.find();
            ParseObject.pinAll(list);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!isTestMode()) {
            // Register client with Parse (for push notifications)
            ParseInstallation.getCurrentInstallation().saveInBackground();

            // Subscribe to channel to listen for updates to the restaurant objects, e.g. waittime
            // modifications per object from cloud code
            ParsePush.subscribeInBackground("restaurantUpdates");
        }
    }
}
