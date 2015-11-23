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
 * This adapter is meant to turn information about a Restaurant into a listview item. That is,
 * the information retrieved by this adapter will be used as an individual chunk of information
 * for building a single item on the RestaurantListUI
 */
public class RestaurantListAdapter
        extends ParseRecyclerQueryAdapter<Restaurant, RestaurantListAdapter.RestaurantViewHolder>
        implements RestaurantChangeObserver {

    private RestaurantManager.RestaurantSortType currentSortType = RestaurantManager.RestaurantSortType.NAME;

    @Override
    /**
     * Called when we need to update the corresponding Restaurant view
     */
    public void update(Restaurant updatedRestaurant) {
        for (int i = 0; i < getItemCount(); i++) {
            Restaurant oldRestaurant = getItem(i);
            if (oldRestaurant.getId().equals(updatedRestaurant.getId())) {

                // check if we need to update sorting
                if (oldRestaurant.getWaitInMinutes() != updatedRestaurant.getWaitInMinutes()) {
                    sortAndUpdate(currentSortType);
                } else {
                    // TODO make more efficient by only updating that single restaurant rather than
                    // requerying parse for all
                    super.loadObjects(); // temporary: simply reload all restaurant objects from parsequery

                }
                break;
            }
        }
    }

    public void updateAll(ManagerRefreshCallback updateCompleteCallback) {
        RestaurantManager.getInstance().refreshAllRestaurantsHard(updateCompleteCallback);
    }

    public void sortAndUpdate(final RestaurantManager.RestaurantSortType sortType) {
        this.currentSortType = sortType;
        ParseQueryAdapter.QueryFactory<Restaurant> newFactory = new ParseQueryAdapter.QueryFactory<Restaurant>() {
            public ParseQuery<Restaurant> create() {
                return RestaurantManager.getInstance().queryForAllRestaurants(sortType);
            }
        };
        // update query factory with new query type
        super.setQueryFactory(newFactory);
        // invalidate current set of loaded restaurants. reload!
        super.loadObjects();
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
            cardView = (CardView) itemView.findViewById(R.id.restaurant_card);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            currentWait = (TextView) itemView.findViewById(R.id.restaurantWaitTime);
            updateButton = (Button) itemView.findViewById(R.id.updateButton);
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

    // TODO: Re-sortAndUpdate/sortAndUpdate restaurant methods

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * Required method for RecyclerView. Upon creation, inflate the appropriate
     * Layout XML view and instantiate a RestaurantViewHolder.
     *
     * @param viewGroup
     * @param i
     * @return
     */
    public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int layout = R.layout.restaurant_list_item;

        final Restaurant restaurant = getItem(i);

        // load different XML for cards showing closed restaurant
        if (!restaurant.isOpenNow()) {
            layout = R.layout.restaurant_list_item_closed;
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout,
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

        if (holder.currentWait != null) {
            // Wait time label
            holder.currentWait.setText(String.format(context.getResources().getString(R.string.restaurant_card_minute_wait),
                    restaurant.getWaitInMinutes()));
            WaitTimeGroup waitTimeGroup = restaurant.getWaitTimeGroup();


            // set color of currentWait label to green/orange/red
            int color;
            switch (waitTimeGroup.getCurrentWait()) {
                case LOW:
                default:
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
            holder.currentWait.setTextColor(context.getResources().getColor(color));
        }

        if (holder.updateButton != null) {

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

}

