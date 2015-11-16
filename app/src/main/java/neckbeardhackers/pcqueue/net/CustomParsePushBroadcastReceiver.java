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
            RestaurantManager.getInstance().refreshIndividualRestaurantHard(restaurantId);
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
