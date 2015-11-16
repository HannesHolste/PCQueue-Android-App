package neckbeardhackers.pcqueue.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.model.Restaurant;
import neckbeardhackers.pcqueue.model.RestaurantManager;
import neckbeardhackers.pcqueue.model.WaitTimeGroup;

public class ReporterActivity extends AppCompatActivity {

    private Restaurant restaurant = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.reportToolbar);
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

        // If we instantaited this ReporterActivity with an intent, unpack the restaurant
        if (intent != null) {
            String restaurantId = intent.getStringExtra("restaurantId");
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
            TextView restaurantName = (TextView) findViewById(R.id.reporter_restaurantName);
            restaurantName.setText(restaurant.getName());
        }

        // Setup wait time selector
        final Spinner spinner = (Spinner) findViewById(R.id.people_spinner);

        final WaitTimeSpinnerAdapter adapter = new WaitTimeSpinnerAdapter(this,
                R.layout.spinner_item,
                WaitTimeGroup.WAIT_TIME_GROUPS.getWaitTimes());
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


        // add click listener to update button
        Button updateButton = (Button) findViewById(R.id.reporter_updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish activity and submit report. Show toast in main UI.
                WaitTimeGroup selectedWaitTimeGroup = (WaitTimeGroup) spinner.getSelectedItem();
                // "Pull" the data from the activity
                HashMap<String, Object> params = new HashMap<String, Object>();
                int time = selectedWaitTimeGroup.getCurrentWait().getClockTimeHigh();
                // Fallout if the activity's restaurant instance is null to prevent NULL_PTR_ERR
                if (restaurant == null) finish();
                String name = restaurant.getName();
                // JSON format e.g. {"name":"D'Lush","time":5}
                params.put("name", name);
                params.put("time", time);
                Log.d("updateButton", "Siphoning request off to Parse-land");
                ParseCloud.callFunctionInBackground("attemptUpdate", params, new FunctionCallback<String>() {
                    public void done(String results, ParseException e) {
                        if (e == null) {
                            Log.d("updateButton", "RESPONSE: " + results);
                            // Successful toast
                            Context context = getApplicationContext();
                            CharSequence text = "Thank you for your update!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            Log.d("updateButton", "ERROR: " + e);
                            // Unsuccessful toast
                            Context context = getApplicationContext();
                            CharSequence text = "There was a problem with submitting your update to the server";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
                finish();
            }
        });
    }
}
