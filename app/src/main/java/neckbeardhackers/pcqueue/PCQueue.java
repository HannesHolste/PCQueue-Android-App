package neckbeardhackers.pcqueue;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

        // Initialize Parse client with authentication information
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

        // Enable anonymous users, so we can save data to the local data store without logging in
        // We shall never log them out and anonymoous users can sync with Parse cloud
        ParseUser.enableAutomaticUser();

        // Setup default Access Control List (ACL), related to write/read permissions
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true /* give access to current user */);

    }
}
