package neckbeardhackers.pcqueue.view;

import android.content.Context;
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
import neckbeardhackers.pcqueue.model.OperatingHours;
import neckbeardhackers.pcqueue.model.Restaurant;
import neckbeardhackers.pcqueue.model.RestaurantManager;

/**
 * Created by brianna lam and katherine duan on 11/21/15.
 */
public class RestaurantInfoActivity extends AppCompatActivity{

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
                if (results.size() > 1) {
                    throw new ParseException(ParseException.OBJECT_NOT_FOUND,
                            "More than one objectId for restaurant " + restaurantId);
                }

                restaurant = results.get(0);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (restaurant != null) {
            TextView restaurantName = (TextView) findViewById(R.id.info_restaurantName);
            restaurantName.setText(restaurant.getName());

            TextView waitTime = (TextView) findViewById(R.id.restaurantWaitTime);
            TextView openSign = (TextView) findViewById(R.id.info_openNow);
            System.out.println("Is " + restaurant.getName() + " open now? " + restaurant.getHours().isOpenNow());
            System.out.println("What is the current wait in minutes for this restaurant? " + restaurant.getWaitInMinutes());
            //waitTime.setText(restaurant.getWaitInMinutes() + " minutes");
            if (restaurant.getHours().isOpenNow()) {
                waitTime.setText(restaurant.getWaitInMinutes() + " minutes");
                openSign.setText("Open Now");
                openSign.setTextColor(getResources().getColor(R.color.textColorHighlight));
            } else {
                waitTime.setText("Not Open");
                waitTime.setTextColor(getResources().getColor(R.color.grey));
                openSign.setText("Closed Now");
                openSign.setTextColor(getResources().getColor(R.color.red));
            }

            int[] hourInfo = {R.id.info_sundayHours, R.id.info_mondayHours, R.id.info_tuesdayHours,
                    R.id.info_wednesdayHours, R.id.info_thursdayHours, R.id.info_fridayHours, R.id.info_saturdayHours};
            TextView fillDays;
            for (int i = 0; i < OperatingHours.DAY_NAMES.length; i++) {
                fillDays = (TextView) findViewById(hourInfo[i]);
                if(!restaurant.getHours().getOperatingHours(OperatingHours.DAY_NAMES[i]).isClosed())
                    fillDays.setText(restaurant.getHours().getOperatingHours(OperatingHours.DAY_NAMES[i]).getOpeningTimeString() +
                        "-\n" + restaurant.getHours().getOperatingHours(OperatingHours.DAY_NAMES[i]).getCloseTimeString());
                else
                    fillDays.setText("Closed");
            }

            TextView infoDescription = (TextView) findViewById(R.id.info_descriptionText);
            infoDescription.setText(restaurant.getDescription());
        }
    }
    /*private static void fillDays(){

    }*/
}