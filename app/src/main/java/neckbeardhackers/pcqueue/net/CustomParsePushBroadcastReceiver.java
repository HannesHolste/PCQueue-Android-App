package neckbeardhackers.pcqueue.net;

import android.content.Context;
import android.content.Intent;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GcmBroadcastReceiver;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import neckbeardhackers.pcqueue.model.Restaurant;
import neckbeardhackers.pcqueue.model.RestaurantManager;

/**
 * A custom push notification receiver that keeps push notifications silent,
 * because we currently only use them to notify our data model and views that
 * the restaurant model changed.
 */
public class CustomParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //super.onPushReceive(context, intent); // do not show push notification
        JSONObject pushData = getPushData(intent);

        if (pushData == null || !pushData.has("restaurantObjectId")) {
            return;
        }

        try {
            // Extract id of changed restaurant object
            String restaurantId = pushData.getString("restaurantObjectId");

            // Query the database from network to get the updated restaurant object
            ParseQuery<Restaurant> query = RestaurantManager.getInstance().queryRestaurantById(restaurantId, false);

            // Make async request to avoid blocking current thread
            query.findInBackground(new FindCallback<Restaurant>() {
                @Override
                public void done(final List<Restaurant> objects, ParseException e) {
                    // Successful query
                    if (e == null || objects.size() != 1) {
                        final Restaurant updatedRestaurant = objects.get(0);

                        // Remove previously cached local data storage restaurant object
                        ParseObject.unpinAllInBackground(objects, new DeleteCallback() {
                            public void done(ParseException e) {
                                // Cache the updated restaurant object in the local data storage
                                ParseObject.pinAllInBackground(objects);
                                RestaurantManager.getInstance().notifyObservers(updatedRestaurant);

                            }
                        });
                    } else {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getPushData(Intent intent) {
        try {
            return new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
