package neckbeardhackers.pcqueue.view;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.HashMap;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.model.RestaurantManager;

public class RestaurantListActivity extends AppCompatActivity {
    private int listScrollPosition = 0;

    public synchronized void addScrollAction(int dScroll) {
        listScrollPosition += dScroll;
        changeHeaderImageHeightBasedOnScroll();
    }

    private synchronized void changeHeaderImageHeightBasedOnScroll() {
        ImageView headerImage = (ImageView)findViewById(R.id.restaurant_list_header_image);
        int newHeight = (400 - listScrollPosition);
        if (newHeight < 0)
            newHeight = 0;
        else if (newHeight > 400)
            newHeight = 400;
        System.err.println(newHeight);
        headerImage.setLayoutParams(new LinearLayout.LayoutParams(headerImage.getWidth(), newHeight));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_list);

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
        RecyclerView restaurantListRecycler = (RecyclerView) this.findViewById(R.id.RestaurantListRecycler);
        restaurantListRecycler.setHasFixedSize(true);
        restaurantListRecycler.setLayoutManager(new LinearLayoutManager(this));
        restaurantListRecycler.setAdapter(infoGetter);

        /* Setup pull-to-refresh listener */
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.RestaurantListRefresher);
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
        restaurantListRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //((RestaurantListActivity)recyclerView.getContext()).addScrollAction(dy);
            }
        });
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            final HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("name", data.getStringExtra("name"));
            params.put("time", data.getIntExtra("time", -1));
            final int oldTime = data.getIntExtra("oldTime", -1);
            final String restaurantId = data.getStringExtra("restaurantId");
            // Display a snackbar!
            final View snackbarView = findViewById(R.id.restaurant_list_coord_layout);
            Snackbar reportConfirmation = Snackbar
                .make(snackbarView, "Update Reported!", Snackbar.LENGTH_LONG);

            reportConfirmation.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar undoConfirmation = Snackbar.make(snackbarView, "Report Undone!",
                        Snackbar.LENGTH_SHORT);
                    undoConfirmation.show();
                    RestaurantManager.getInstance().setLocalRestaurantWaitTime(restaurantId, oldTime);
                }
            });

            reportConfirmation.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    if (event != Snackbar.Callback.DISMISS_EVENT_ACTION &&
                            event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE)
                        pushToParse(params);
                }
            });
            reportConfirmation.show();
            RestaurantManager.getInstance().setLocalRestaurantWaitTime(restaurantId, (int) params.get("time"));
        }
    }

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
}
