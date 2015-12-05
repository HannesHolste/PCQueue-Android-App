package neckbeardhackers.pcqueue.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.event.RestaurantChangeObserver;
import neckbeardhackers.pcqueue.model.DailyOperatingHours;
import neckbeardhackers.pcqueue.model.OperatingHours;
import neckbeardhackers.pcqueue.model.Restaurant;
import neckbeardhackers.pcqueue.model.RestaurantManager;
import neckbeardhackers.pcqueue.model.WaitTimeGroup;

/**
 * This class is responsible for creating the info pane activity. It will receive live updates from
 * the parse database and display the current wait time of the corresponding restaurant. It will
 * display the time and necessary information for the user. Also indicates if the restaurant is
 * closed
 */
public class RestaurantInfoActivity extends MasterActivity implements RestaurantChangeObserver {

    private Restaurant restaurant = null;
    @Override

    /**
     * Instantiates the info pane activity
     *
     * @param savedInstanceState The previous state to load from
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerNetworkConnectionListener(this);

        setContentView(R.layout.restaurant_info_pane_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        // back button
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Register for changes
        RestaurantManager.getInstance().registerRestaurantChangeListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            String restaurantId = intent.getStringExtra("restaurantId");
            // create an instance of the restaurant manager
            RestaurantManager restaurantManager = RestaurantManager.getInstance();
            // find the object in the parse database
            ParseQuery<Restaurant> query = restaurantManager.queryRestaurantById(restaurantId);
            try {
                // synchronous, blocking call.
                List<Restaurant> results = query.find();

                // ensure we only got one result
                if (results.size() != 1) {
                    throw new ParseException(ParseException.OBJECT_NOT_FOUND,
                            "More than one objectId for restaurant " + restaurantId);
                }

                restaurant = results.get(0);
                update(restaurant);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Unregisters the info pane activity from receiving updates
     */
    @Override
    public void onStop() {
        RestaurantManager.getInstance().unregisterRestaurantChangeListener(this);
        super.onStop();
    }

    /**
     * updates the restaurant information if there is an update. Also is used in instantiating the
     * Restaurant info pane.
     * @param updatedRestaurant
     */
    @Override
    public void update(Restaurant updatedRestaurant) {
        if (restaurant != null) {
            if (updatedRestaurant.getId().equals(restaurant.getId()))
                restaurant = updatedRestaurant;

            // Set the restaurant name
            TextView restaurantName = (TextView) findViewById(R.id.info_restaurantName);
            restaurantName.setText(restaurant.getName());

            TextView waitTime = (TextView) findViewById(R.id.restaurantWaitTime);
            TextView openSign = (TextView) findViewById(R.id.info_openNow);

            if(restaurant.isOpenNow()){
                waitTime.setText(restaurant.getWaitInMinutes() + " minutes");
                openSign.setText("Open Now");
                openSign.setTextColor(getResources().getColor(R.color.textColorHighlight));
            }
            else {
                waitTime.setText("Not Open");
                openSign.setText("Closed Now");
                waitTime.setTextColor(getResources().getColor(R.color.grey));
                openSign.setTextColor(getResources().getColor(R.color.red));
            }

            // Put all the hour columns into an array so we can loop through them
            int[] hourInfo = {R.id.info_sundayHours, R.id.info_mondayHours, R.id.info_tuesdayHours,
                    R.id.info_wednesdayHours, R.id.info_thursdayHours, R.id.info_fridayHours, R.id.info_saturdayHours};
            TextView fillDays;

            for (int i = 0; i < OperatingHours.DAY_NAMES.length; i++) {
                fillDays = (TextView) findViewById(hourInfo[i]);
                DailyOperatingHours todaysHours = restaurant.getHours().getOperatingHours(OperatingHours.getDayOfWeek(i));
                if(todaysHours.doesNotClose()){
                    fillDays.setText("24 Hours");
                }
                else if(!todaysHours.closedAllDay()) {
                    fillDays.setText(String.format("%s-\n%s", todaysHours.getOpeningTimeString(), todaysHours.getCloseTimeString()));
                }
                else
                    fillDays.setText("Closed");
            }

            // sets the description
            TextView infoDescription = (TextView) findViewById(R.id.info_descriptionText);
            infoDescription.setText(restaurant.getDescription());

            // sets the phone number
            TextView phoneNum = (TextView) findViewById(R.id.info_phoneNumber);
            phoneNum.setText(restaurant.getPhoneNumber());


            WaitTimeGroup waitTimeGroup = restaurant.getWaitTimeGroup();

            // set color of currentWait label to green/orange/red
            int color = -1;
            switch (waitTimeGroup.getCurrentWait()) {
                case LOW:
                    color = R.color.green;
                    break;
                case MEDIUM:
                    color = R.color.orange;
                    break;
                case HIGH:
                case VERY_HIGH:
                    color = R.color.red;
                    break;
            }
            if (color != -1) {
                waitTime.setTextColor(getResources().getColor(color));
            }
        }
    }
}
