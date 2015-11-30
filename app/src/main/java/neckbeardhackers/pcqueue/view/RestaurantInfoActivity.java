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
 * Created by brianna lam and katherine duan on 11/21/15.
 */
public class RestaurantInfoActivity extends MasterActivity implements RestaurantChangeObserver {

    private Restaurant restaurant = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            System.out.println("Restaurant id is: " + restaurantId);
            RestaurantManager restaurantManager = RestaurantManager.getInstance();
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

    @Override
    public void onStop() {
        RestaurantManager.getInstance().unregisterRestaurantChangeListener(this);
        super.onStop();
    }

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
            System.out.println("Is " + restaurant.getName() + " open now? " + restaurant.isClosed());
            System.out.println("What is the current wait in minutes for this restaurant? " + restaurant.getWaitInMinutes());
            //waitTime.setText(restaurant.getWaitInMinutes() + " minutes");
//            if (restaurant.getHours().isOpenNow()) {
//                waitTime.setText(restaurant.getWaitInMinutes() + " minutes");
//                openSign.setText("Open Now");
//                openSign.setTextColor(getResources().getColor(R.color.textColorHighlight));
//            }
            if(restaurant.isClosed() == 0){
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
                    //fillDays.setTextColor(getResources().getColor(R.color.textColorHighlight));
                }
                else if(!todaysHours.closedAllDay()) {
                    fillDays.setText(String.format("%s-\n%s", todaysHours.getOpeningTimeString(), todaysHours.getCloseTimeString()));
                }
                else
                    fillDays.setText("Closed");
            }

            TextView infoDescription = (TextView) findViewById(R.id.info_descriptionText);
            infoDescription.setText(restaurant.getDescription());

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
