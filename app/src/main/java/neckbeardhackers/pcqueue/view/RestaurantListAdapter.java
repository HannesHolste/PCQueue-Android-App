package neckbeardhackers.pcqueue.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ameron32.ParseRecyclerQueryAdapter;
import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.model.Restaurant;

/**
 *
 * This adapter is meant to turn information about a Restaurant into a listview item. That is,
 * the information retrieved by this adapter will be used as an individual chunk of information
 * for building a single item on the RestaurantListUI
 */
public class RestaurantListAdapter extends ParseRecyclerQueryAdapter<Restaurant, RestaurantListAdapter.RestaurantViewHolder> {

    private RestaurantViewHolder restaurantViewHolder;

    /**
     * ViewHolder representing a restaurant card in our UI,
     * based on the restaurant card layout XML,
     * as required by RecyclerView in android.
     */
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView restaurantName;
        TextView currentWait;
        Button updateButton;

        RestaurantViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.restaurant_card);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            currentWait = (TextView) itemView.findViewById(R.id.restaurantWaitTime);
            updateButton = (Button)itemView.findViewById(R.id.updateButton);
        }
    }
    private Context context;

    public RestaurantListAdapter(ParseQueryAdapter.QueryFactory<Restaurant> factory, boolean hasStableIds) {
        super(factory, hasStableIds);
    }

    public RestaurantListAdapter(Context c) {
        // Create query Factory and setup superclass
        super(new ParseQueryAdapter.QueryFactory<Restaurant>() {
            public ParseQuery<Restaurant> create() {
                // Configure a custom Parse query to retrieve restaurants sorted alphabetically
                ParseQuery<Restaurant> query = Restaurant.getQuery();
                query.fromLocalDatastore();

                // sort alphabetically by restaurant name
                query.orderByAscending("Name");
                return query;
            }
        }, false);

        this.context = c;
    }

    // TODO: Re-sort/sort restaurant methods

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * Required method for RecyclerView. Upon creation, inflate the appropriate
     * Layout XML view and instantiate a RestaurantViewHolder.
     * @param viewGroup
     * @param i
     * @return
     */
    public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.restaurant_list_item,
                                                                    viewGroup, false);
        this.restaurantViewHolder = new RestaurantViewHolder(v);
        return restaurantViewHolder;
    }

    @Override

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        final Restaurant restaurant = getItem(position);

        restaurantViewHolder.restaurantName.setText(restaurant.getName());
        //restaurantViewHolder.currentWait.setText(restaurantList.get(i).getWaitTime().getCurrentWait());
        restaurantViewHolder.updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReporterActivity.class);
                intent.putExtra("restaurantId", restaurant.getId());
                v.getContext().startActivity(intent);
            }

        });


    }

}

