package neckbeardhackers.pcqueue.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.model.Restaurant;
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

        if (intent != null) {
            // TODO use Parceable and serialize Restaurant object
            //this.restaurant = new Restaurant(intent.getStringExtra("restaurant"));
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
                // TODO Submit Report to Parse (asynchronously!!)
                WaitTimeGroup selectedWaitTimeGroup = (WaitTimeGroup) spinner.getSelectedItem();
                finish();
            }
        });
    }
}
