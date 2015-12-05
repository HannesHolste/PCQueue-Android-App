package neckbeardhackers.pcqueue.net;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GcmBroadcastReceiver;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import neckbeardhackers.pcqueue.model.Restaurant;
import neckbeardhackers.pcqueue.model.RestaurantManager;

/**
 * A custom push notification receiver that keeps push notifications silent,
 * because we currently only use them to notify our data model and views that
 * the restaurant model changed.
 */
public class CustomParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    /**
     * Receives a signal from Parse indicating that something needs to be changed. If the thing that
     * needs to be changed is a restaurant, delegates to the RestaurantManager to get the new
     * restaurant from the database
     *
     * @param context The in-app context that is pushing the update
     * @param intent An intent contain the data from the Parse push
     */
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //super.onPushReceive(context, intent); // do not show push notification
        JSONObject pushData = getPushData(intent);

        if (pushData == null
                || (!pushData.has("restaurantObjectId") && !pushData.has("restaurantObjectIds"))) {
            Log.d("onPushReceive", "Fields didn't match");
            return;
        }

        if (pushData.has("restaurantObjectId")) {
            Log.d("onPushReceive", "Got a JSON object with a single restaurant");
            // This is for the single restaurant case
            try {
                // Extract id of changed restaurant object
                String restaurantId = pushData.getString("restaurantObjectId");
                RestaurantManager.getInstance().refreshIndividualRestaurantHard(restaurantId);
            } catch (JSONException e) {
                Log.d("onPushReceive", "Had a problem for: Got a JSON object with a single restaurant");
                e.printStackTrace();
            }
        } else if (pushData.has("restaurantObjectIds")) {
            Log.d("onPushReceive", "Got a JSON object with a MANY single restaurant");
            // Sometimes we want to update all the restaurants at once (e.g. on a decremement)
            // and so the server sends us all the restaurant data
            Log.d("onPushReceive", "Updating MANY restaurant");
            RestaurantManager.getInstance().refreshAllRestaurantsHard();
        }

    }

    /**
     * Converts the received parse data into a JSONObject
     * @param intent The intent containing data from the parse push
     * @return The converted JSONObject from the parse data
     */
    private JSONObject getPushData(Intent intent) {
        try {
            return new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}