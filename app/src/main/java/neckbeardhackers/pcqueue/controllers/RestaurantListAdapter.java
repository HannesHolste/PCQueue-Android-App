package neckbeardhackers.pcqueue.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

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
        implements RestaurantChangeObserver{

    // Sorts the landing page by name by default
    private RestaurantManager.RestaurantSortType currentSortType = RestaurantManager.RestaurantSortType.NAME;
    // open and close restaurant indicators
    public static final int CARD_VIEW_OPEN_RESTAURANT = 0;
    public static final int CARD_VIEW_CLOSED_RESTAURANT = 1;

    private Context context;

    /**
     * Creates a RestaurantListAdapter object and holds the restaurants so that they can be displayed
     * @param c Context at which this RestaurantListAdapter will be in
     */
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

    /**
     * Called when we need to update the corresponding Restaurant view
     * @param updatedRestaurant the updated restaurant to extract new information about
     */
    @Override
    public void update(Restaurant updatedRestaurant) {
        for (int i = 0; i < getItemCount(); i++) {
            Restaurant oldRestaurant = getItem(i);
            if (oldRestaurant.getId().equals(updatedRestaurant.getId())) {
                sortAndUpdateForcingASingleRestaurant(currentSortType, null, updatedRestaurant, false);
                break;
            }
        }
    }

    /**
     * Updates all of the restaurant cards with a update callback
     * @param updateCompleteCallback
     */
    public void updateAll(ManagerRefreshCallback updateCompleteCallback) {
        RestaurantManager.getInstance().refreshAllRestaurantsHard(updateCompleteCallback);
    }

    /**
     * Sorts the restaurant and force updates a particular restaurant
     * @param sortType The sorting order to order the restaurant cards
     * @param callback The call back function after the sorting is done
     * @param forcedUpdate The restaurant to force update its information
     * @param animations Animates the moving of the cards when the sorting changes
     */
    public void sortAndUpdateForcingASingleRestaurant(final RestaurantManager.RestaurantSortType sortType,
                                                      final ManagerRefreshCallback callback,
                                                      final Restaurant forcedUpdate, final boolean animations) {
        this.currentSortType = sortType;
        ParseQueryAdapter.QueryFactory<Restaurant> newFactory = new ParseQueryAdapter.QueryFactory<Restaurant>() {
            public ParseQuery<Restaurant> create() {
                return RestaurantManager.getInstance().queryForAllRestaurants(sortType);
            }
        };

        ParseQuery<Restaurant> sortedQuery = newFactory.create();
        sortedQuery.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> objects, ParseException e) {
                if (e != null)
                    return;

                for (int i = 0; i < objects.size(); ++i) {
                    if ((((getItemCount() - 1) < i || !objects.get(i).hasDifferences(getItem(i))))
                            && (forcedUpdate == null || !objects.get(i).equals(forcedUpdate)))
                        continue;
                    else {
                        if (animations) {
                            removeItem(i);
                            notifyItemRemoved(i);
                            addItem(i, objects.get(i));
                            notifyItemInserted(i);
                        } else {
                            setItem(i, objects.get(i));
                            notifyItemChanged(i);
                        }
                    }
                }
                if (callback != null)
                    callback.handleRefreshComplete();
            }
        });
    }

    /**
     * Sorts the restaurants given a sort while updating the restaurant wait times
     * @param sortType The type of sorting for the restaurant cards
     * @param callback The callback function to call after the sorting is complete
     */
    public void sortAndUpdate(final RestaurantManager.RestaurantSortType sortType,
                              final ManagerRefreshCallback callback) {
        sortAndUpdateForcingASingleRestaurant(sortType, callback, null, false);

    }

    /**
     * Sorts and updates the restaurants given just a sort type and no call back
     * @param sortType The sorting order for the restaurant cards
     */
    public void sortAndUpdate(final RestaurantManager.RestaurantSortType sortType) {
        sortAndUpdate(sortType, null);
    }


    /**
     * ViewHolder representing a restaurant card in our UI, based on the restaurant card layout XML,
     * as required by RecyclerView in android.
     */
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        // card properties
        CardView cardView;
        TextView restaurantName;
        TextView currentWait;
        Button updateButton;

        // card constructor
        RestaurantViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.restaurant_card);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
            currentWait = (TextView) itemView.findViewById(R.id.restaurantWaitTime);
            updateButton = (Button) itemView.findViewById(R.id.updateButton);
        }
    }

    /**
     * Getter function to obtain the number of restaurants in the ParseQuery
     * @return the number of restaurants
     */
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    @Override
    // load different XML for cards showing closed restaurant
    public int getItemViewType(int position) {
        return getItem(position).isOpenNow() ? CARD_VIEW_OPEN_RESTAURANT : CARD_VIEW_CLOSED_RESTAURANT;
    }

    /**
     * Required method for RecyclerView. Upon creation, inflate the appropriate
     * Layout XML view and instantiate a RestaurantViewHolder.
     *
     * @param viewGroup The group that this view holder belongs in
     * @param viewType Open or closed restaurant
     * @return Holder corresponding to a restaurant card
     */
    public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layout;

        // load different XML for cards showing closed restaurant
        switch (viewType) {
            case CARD_VIEW_CLOSED_RESTAURANT:
                layout = R.layout.restaurant_list_item_closed;
                break;
            case CARD_VIEW_OPEN_RESTAURANT:
            default:
                layout = R.layout.restaurant_list_item;
                break;
        }


        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout,
                viewGroup, false);
        return new RestaurantViewHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    @Override
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
                    RestaurantListActivity encapsulatedActivity = (RestaurantListActivity) context;
                    encapsulatedActivity.startActivityForResult(intent, 100);

                }

            });
        }

        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), RestaurantInfoActivity.class);
                    intent.putExtra("restaurantId", restaurant.getId());
                    v.getContext().startActivity(intent);
                }
            });

        }

    }
}

