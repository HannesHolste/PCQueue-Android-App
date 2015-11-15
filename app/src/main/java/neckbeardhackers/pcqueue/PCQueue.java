package neckbeardhackers.pcqueue;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import neckbeardhackers.pcqueue.model.Restaurant;

/**
 * Main entry point for the application
 */
public class PCQueue extends Application {

    // TODO: Extract this to a separate configuration file, and re-generate keys
    // Leaving keys in source code that is committed to github is poor practice
    private final String PARSE_APP_ID = "***REMOVED***";
    private final String PARSE_CLIENT_KEY = "***REMOVED***";


    /**
     * When the application is run first, run initialization procedures.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable storing data locally first. We'll take care of syncing it.
        Parse.enableLocalDatastore(getApplicationContext());

        // Register Parse Object subclasses that we use to represent our data model
        // and that are kept in sync with Parse
        ParseObject.registerSubclass(Restaurant.class);

        // Initialize Parse client with authentication information
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

        // Enable anonymous users, so we can save data to the local data store without logging in
        // We shall never log them out and anonymoous users can sync with Parse cloud
        ParseUser.enableAutomaticUser();

        // Setup default Access Control List (ACL), related to write/read permissions
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true /* give access to current user */);

        // Pin all Restaurant objects so they're accessible from the local data store
        ParseQuery<Restaurant> query = Restaurant.getQuery();
        try {
            // TODO: Show a loading app indicator in the UI while this is happening
            ParseObject.pinAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
