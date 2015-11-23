package neckbeardhackers.pcqueue.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.model.RestaurantManager;

public class RestaurantListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_list);

        // This will locate the toolbar in activity_restaurant_list and
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);



        /* Load the restaurant list data */
        final RestaurantListAdapter infoGetter = new RestaurantListAdapter(this);
        RecyclerView restaurantListRecycler = (RecyclerView) this.findViewById(R.id.RestaurantListRecycler);
        restaurantListRecycler.setHasFixedSize(true);
        restaurantListRecycler.setLayoutManager(new LinearLayoutManager(this));
        restaurantListRecycler.setAdapter(infoGetter);

        // Sorting button event listeners
        Button sortByWaitButton = (Button) findViewById(R.id.sortByWaitButton);
        Button sortByNameButton = (Button) findViewById(R.id.sortByNameButton);

        sortByNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoGetter.sortAndUpdate(RestaurantManager.RestaurantSortType.NAME);
            }
        });
        sortByWaitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoGetter.sortAndUpdate(RestaurantManager.RestaurantSortType.WAIT_TIME);
            }
        });

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
    }
}
