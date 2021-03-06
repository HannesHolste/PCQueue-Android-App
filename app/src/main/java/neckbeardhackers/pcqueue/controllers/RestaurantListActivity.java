package neckbeardhackers.pcqueue.controllers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.HashMap;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.model.RestaurantManager;

/**
 * This class is responsible for creating the landing page for the application. It will listen for
 * network connection and display all the restaurants with their wait times. It will be sorted by
 * name, alphabetically.
 */
public class RestaurantListActivity extends MasterActivity {

    // Instance of SwipeRefreshLayout that will listen for manual refreshes
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * This will create the landing page for the application with the restaurant list, establish a
     * listener for the network, color the appropriate items, and listen for which sort button
     * is clicked. Also listens for manual refreshing of the cards' wait times.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_list);

        registerNetworkConnectionListener(this);

        // This will locate the toolbar in activity_restaurant_list and
        Toolbar toolbar = (Toolbar) findViewById(R.id.restaurant_list_toolbar);
        setSupportActionBar(toolbar);


        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set the transparent color of the status bar, 10% darker
        tintManager.setTintColor(Color.parseColor("#10000000"));

        /* Load the restaurant list data */
        final RestaurantListAdapter infoGetter = new RestaurantListAdapter(this);
        final RecyclerView restaurantListRecycler = (RecyclerView) this.findViewById(R.id.RestaurantListRecycler);
        restaurantListRecycler.setHasFixedSize(true);
        restaurantListRecycler.setLayoutManager(new LinearLayoutManager(this));
        restaurantListRecycler.setAdapter(infoGetter);

        // Sorting button event listeners
        final Button sortByWaitButton = (Button) findViewById(R.id.sortByWaitButton);
        final Button sortByNameButton = (Button) findViewById(R.id.sortByNameButton);

        // the bars that will appear underneath the respective sort that is active
        final View sortWaitTimeUnderline = findViewById(R.id.waitTimeSortActive);
        final View sortNameUnderline = findViewById(R.id.nameSortActive);

        // sorts the restaurant cards by name and highlights the SORT BY NAME button
        sortByNameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                infoGetter.sortAndUpdateForcingASingleRestaurant(RestaurantManager.RestaurantSortType.NAME,
                        new RestaurantManager.ManagerRefreshCallback() {
                            @Override
                            public void handleRefreshComplete() {
                                restaurantListRecycler.scrollToPosition(0);
                            }
                        }, null, true);

                // highlighting and greying out the sorts depending on which one is clicked
                sortByNameButton.setTextAppearance(getApplicationContext(), R.style.sortButton_active);
                sortByWaitButton.setTextAppearance(getApplicationContext(), R.style.sortButton_inactive);

                // set icons
                sortByNameButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_sort_name_active, 0, 0, 0);
                sortByWaitButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_sort_wait_inactive, 0, 0, 0);


                // Making the inactive sort bar invisible and make the other visible
                sortWaitTimeUnderline.setVisibility(View.INVISIBLE);
                sortNameUnderline.setVisibility(View.VISIBLE);

                // setting the color to white (This is done because when the app opens for the first time,
                // the bar will by default be underneath SORT BY NAME since we sort it alphabetically first
                sortNameUnderline.setBackgroundResource(R.color.textColorSecondary);
            
            }
        });

        // Sorts the restaurant cards by wait time and highlights the SORT BY WAIT TIME button
        sortByWaitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoGetter.sortAndUpdateForcingASingleRestaurant(RestaurantManager.RestaurantSortType.WAIT_TIME,
                        new RestaurantManager.ManagerRefreshCallback() {

                            @Override
                            public void handleRefreshComplete() {
                                restaurantListRecycler.scrollToPosition(0);
                            }
                        }, null, true);


                sortByWaitButton.setTextAppearance(getApplicationContext(), R.style.sortButton_active);
                sortByNameButton.setTextAppearance(getApplicationContext(), R.style.sortButton_inactive);

                // set icons
                sortByNameButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_sort_name_inactive, 0, 0, 0);
                sortByWaitButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_sort_wait_active, 0, 0, 0);


                sortNameUnderline.setVisibility(View.INVISIBLE);
                sortWaitTimeUnderline.setVisibility(View.VISIBLE);
                sortWaitTimeUnderline.setBackgroundResource(R.color.textColorSecondary);
            }
        });

        sortByNameButton.callOnClick();

        /* Setup pull-to-refresh listener */
        swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.RestaurantListRefresher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                infoGetter.updateAll(new RestaurantManager.ManagerRefreshCallback() {

                    @Override
                    public void handleRefreshComplete() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

    }

    /**
     * This function will obtain the data returned after the user submits a queue report.
     * It will hold the data for a certain amount of time and then release it to the server
     * once the popbar disappears.
     * @param requestCode request for result (unused)
     * @param resultCode unused
     * @param data parse information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            final HashMap<String, Object> params = new HashMap<String, Object>();
            // restaurant name
            params.put("name", data.getStringExtra("name"));
            // wait time
            params.put("time", data.getIntExtra("time", -1));
            final int oldTime = data.getIntExtra("oldTime", -1);
            final String restaurantId = data.getStringExtra("restaurantId");
            // Display a snackbar!
            final View snackbarView = findViewById(R.id.restaurant_list_coord_layout);
            Snackbar reportConfirmation = Snackbar
                .make(snackbarView, "Update Reported!", Snackbar.LENGTH_LONG);

            // Hold the report data momentarily
            reportConfirmation.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar undoConfirmation = Snackbar.make(snackbarView, "Report Undone!",
                        Snackbar.LENGTH_SHORT);
                    undoConfirmation.show();
                    // sets the wait time locally on the device
                    RestaurantManager.getInstance().setLocalRestaurantWaitTime(restaurantId, oldTime);
                }
            });

            reportConfirmation.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    // checking if the snackbar was not dismissed and there is no second popbar
                    // that shows up, push the report to parse database
                    if (event != Snackbar.Callback.DISMISS_EVENT_ACTION &&
                            event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE)
                        pushToParse(params);
                }
            });
            reportConfirmation.show();
            RestaurantManager.getInstance().setLocalRestaurantWaitTime(restaurantId, (int) params.get("time"));
        }
    }

    /**
     * This function will push report queue data to the parse database once the undo button disappears
     *
     * @param params Restaurant queue report
     */
    final void pushToParse(HashMap<String, Object> params) {
        ParseCloud.callFunctionInBackground("attemptUpdate", params, new FunctionCallback<String>() {
        public void done(String results, ParseException e) {
            final View snackbarView = findViewById(R.id.restaurant_list_coord_layout);
            if (e == null) {
                Log.d("updateButton", "RESPONSE: " + results);
            } else {
                Log.d("updateButton", "ERROR: " + e);
                // Unsuccessful snackbar
                Snackbar failureConfirmation = Snackbar.make(snackbarView,
                        "Report Failed to Send!", Snackbar.LENGTH_LONG);
                failureConfirmation.show();
            }
        }
    });
    }

    /**
     * If we lose connectivity, we should disable the pull-to-refresh capabilities
     *
     * @param hasConnection: whether or not internet is working
     **/
    @Override
    public void onNetworkConnectivityChange(boolean hasConnection) {
        if (swipeRefreshLayout != null) {
            if (hasConnection) {
                Log.d("swipeToRefresh", "Enabling swipe-to-refresh, hasConnection="+hasConnection);
                swipeRefreshLayout.setEnabled(true);
            } else {
                Log.d("swipeToRefresh", "Disabling swipe-to-refresh, hasConnection="+hasConnection);
                swipeRefreshLayout.setEnabled(false);
            }
        }
        // Do the stuff from the parent method (show the "no-internet" bar)
        super.onNetworkConnectivityChange(hasConnection);
    }

}
