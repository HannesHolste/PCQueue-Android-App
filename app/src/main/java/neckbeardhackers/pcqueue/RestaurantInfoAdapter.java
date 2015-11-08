package neckbeardhackers.pcqueue;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brandon on 10/25/15.
 *
 * This adapter is meant to turn information about a Restaurant into a listview item. That is,
 * the information retrieved by this adapter will be used as an individual chunk of information
 * for building a single item on the RestaurantListUI
 */
public class RestaurantInfoAdapter extends RecyclerView.Adapter<RestaurantInfoAdapter.RestaurantViewHolder> {

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
    private List<Restaurant> restaurantList;
    private Context context;

    public RestaurantInfoAdapter(Context c) {
        this.context = c;
        this.restaurantList = new List<Restaurant>();
    }

    public RestaurantInfoAdapter(List<Restaurant> restaurants) {
        this.restaurantList = restaurants;
    }

    public void instantiateList() {
        if (restaurantList == null) {
            restaurantList = Restaurant.getSampleData();
        }
    }

    @Override
    public int getItemCount() {
        return this.restaurantList.size();
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.restaurant_list_item,
                                                                    viewGroup, false);
        return new RestaurantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder restaurantViewHolder, int i) {
        restaurantViewHolder.restaurantName.setText(restaurantList.get(i).getRestaurantName());
        restaurantViewHolder.currentWait.setText(restaurantList.get(i).getWaitTime());
        restaurantViewHolder.updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: GO TO UPDATE ACTIVITY
            }

        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    }

}
