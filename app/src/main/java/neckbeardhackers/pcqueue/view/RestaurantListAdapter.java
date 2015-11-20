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

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.event.RestaurantChangeObserver;
import neckbeardhackers.pcqueue.model.Restaurant;
import neckbeardhackers.pcqueue.model.RestaurantManager;
import neckbeardhackers.pcqueue.model.RestaurantManager.ManagerRefreshCallback;
import neckbeardhackers.pcqueue.model.WaitTimeGroup;

/**
 *
 * This adapter is meant to turn information about a Restaurant into a listview item. That is,
 * the information retrieved by this adapter will be used as an individual chunk of information
 * for building a single item on the RestaurantListUI
 */
public class RestaurantListAdapter
        extends ParseRecyclerQueryAdapter<Restaurant, RestaurantListAdapter.RestaurantViewHolder>
        implements RestaurantChangeObserver {

    @Override
    /**
     * Called when we need to update the corresponding Restaurant view
     */
    public void update(Restaurant updatedRestaurant) {
        for (int i = 0; i < getItemCount(); i++) {
            Restaurant r = getItem(i);
            if (r.getId().equals(updatedRestaurant.getId())) {
                // Update the view for this restaurant
                super.loadObjects(); // temporary: simply reload all restaurant objects from parsequery
                // TODO: Make this more efficient by updating only one restaurant
                break;
            }
        }
    }

    public void updateAll(ManagerRefreshCallback updateCompleteCallback) {
        RestaurantManager.getInstance().refreshAllRestaurantsHard(updateCompleteCallback);
    }


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

    public RestaurantListAdapter(Context c) {
        // Create query Factory and setup superclass
        super(new ParseQueryAdapter.QueryFactory<Restaurant>() {
            public ParseQuery<Restaurant> create() {
              return RestaurantManager.getInstance().queryForAllRestaurants();
            }
        }, false);

        this.context = c;
        RestaurantManager.getInstance().registerRestaurantChangeListener(this);
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
        return new RestaurantViewHolder(v);
    }

    @Override

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        final Restaurant restaurant = getItem(position);

        // Restaurant name
        holder.restaurantName.setText(restaurant.getName());

        // Wait time label
        holder.currentWait.setText(restaurant.getWaitInMinutes() + " minute wait");
        WaitTimeGroup waitTimeGroup = restaurant.getWaitTimeGroup();

        //DailyOperatingHours temp = new DailyOperatingHours();
        System.out.println("The restaurant is: "+ restaurant.getName());
        System.out.println("Is it open? " + restaurant.getHours().isOpenNow());
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
            holder.currentWait.setTextColor(context.getResources().getColor(color));
        }

        if (!restaurant.getHours().isOpenNow()) {
            // TODO: Make the closed restaurant cards actually look closed. The below grey toggle
            // looks like absolute poopy garbage
            holder.cardView.setCardBackgroundColor(R.color.grey);
        }

        holder.updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReporterActivity.class);
                intent.putExtra("restaurantId", restaurant.getId());
                v.getContext().startActivity(intent);
            }

        });


    }

}

