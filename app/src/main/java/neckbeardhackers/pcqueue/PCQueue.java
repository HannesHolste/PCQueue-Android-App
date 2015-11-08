package neckbeardhackers.pcqueue;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

public class PCQueue extends Application {
    private final String PARSE_APP_ID = "***REMOVED***";
    private final String PARSE_CLIENT_KEY = "***REMOVED***";

    @Override
    public void onCreate() {
        super.onCreate();

        initializeParse();
    }

    public void initializeParse() {
        // Enable Local Datastore and initialize Parse
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
    }
}
