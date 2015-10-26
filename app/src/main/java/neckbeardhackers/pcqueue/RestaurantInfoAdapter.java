package neckbeardhackers.pcqueue;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brandon on 10/25/15.
 *
 * This adapter is meant to turn information about a Restaurant into a listview item. That is,
 * the information retrieved by this adapter will be used as an individual chunk of information
 * for building a single item on the RestaurantListUI
 */
public class RestaurantInfoAdapter extends BaseAdapter {
    private List<Restaurant> restaurantList;
    private Context context;

    public RestaurantInfoAdapter(Context c) {
        this.context = c;
    }

    public void instantiateList() {
        if (restaurantList == null) {
            restaurantList = Restaurant.getSampleData();
        }
    }

    @Override
    public int getCount() {
        this.instantiateList();
        return this.restaurantList.size();
    }

    @Override
    public Object getItem(int position) {
        this.instantiateList();
        return this.restaurantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.restaurant_list_item, parent, false);
        }

        TextView resName = (TextView) convertView.findViewById(R.id.restaurantName);
        TextView waitTime = (TextView) convertView.findViewById(R.id.restaurantWaitTime);
        resName.setText(this.restaurantList.get(position).getRestaurantName());
        waitTime.setText(String.format("~%d Min.", this.restaurantList.get(position).getWaitTime().getCurrentWait()));

        return convertView;
    }
}
