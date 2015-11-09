package neckbeardhackers.pcqueue;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Report extends AppCompatActivity {

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.reportToolbar);
        setSupportActionBar(toolbar);


        // Setup wait time selector
        final Spinner spinner = (Spinner) findViewById(R.id.people_spinner);

        final WaitTimeSpinnerAdapter adapter = new WaitTimeSpinnerAdapter(this,
                R.layout.spinner_item,
                WaitTime.WaitTimeByGroup.getWaitTimes());
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


        // add click listener to update button
        Button updateButton = (Button) findViewById(R.id.reporter_updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish activity and submit report. Show toast in main UI.
                // TODO Submit Report to Parse (asynchronously!!)
                WaitTime selectedWaitTime = (WaitTime) spinner.getSelectedItem();
                finish();
            }
        });
    }
}
