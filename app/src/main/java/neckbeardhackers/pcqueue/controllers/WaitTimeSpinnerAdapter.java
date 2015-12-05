package neckbeardhackers.pcqueue.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import neckbeardhackers.pcqueue.model.WaitTimeGroup;

/**
 * The Adapter for a spinner containing WaitTimes that is to be used on the ReportPage, allowing
 * a Spinner to select from the various WaitTime groupings that have been defined
 * Created by hannes on 11/8/15.
 */
public class WaitTimeSpinnerAdapter extends ArrayAdapter<WaitTimeGroup> {
    private Context context;

    private WaitTimeGroup[] values;
    private int textViewResourceId;
    private int dropDownResourceId;

    /**
     * Primary constructor. Creates the adapter with information about the Spinner itself so that
     * it may properly fill it
     * @param context The context that is creating the adapter (The activity containing the Spinner)
     * @param textViewResourceId The ID of the TextView pertaining to the spinner
     * @param values The values to have inside the spinner
     */
    public WaitTimeSpinnerAdapter(Context context, int textViewResourceId,
                                  WaitTimeGroup[] values) {
        super(context, textViewResourceId, values);
        this.textViewResourceId = textViewResourceId;
        this.dropDownResourceId = textViewResourceId;
        this.context = context;
        this.values = values;
    }

    /**
     * Handles the ArrayAdapter method by returning the amount of items we have within our Spinner
     * @return The number of items in our spinner
     */
    @Override
    public int getCount() {
        return values.length;
    }

    /**
     * Handles the ArrayAdapter method by returning the item at the specified position
     * @param position The position of the item to get
     * @return The item at the specified position
     */
    @Override
    public WaitTimeGroup getItem(int position) {
        return values[position];
    }

    /**
     * Handles the ArrayAdapter method by returning the item position as an id at the given position
     * @param position The position of the item to return the ID of
     * @return The ID (Which is really just the position) of the item at the given position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Handles the ArrayAdapter method by setting the DropDown (where the data is) to the provided
     * resource
     * @param resource The DropDownView to be assigned the items
     */
    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
        this.dropDownResourceId = resource;
    }

    /**
     * Handles the ArrayAdapter method by getting the view corresponding to the item at a given
     * position
     * @param position The position of the item to get the View for
     * @param convertView The view to convert into (not used)
     * @param parent The parent of the View we are establishing
     * @return The view corresponding to the item at the given position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView label = (TextView) inflater.inflate(textViewResourceId, null);

        label.setText(values[position].toString());

        return label;
    }


    /**
     * Handles the ArrayAdapter method by getting the DropDownView with the item at the given positon
     * @param position The item to initialize the DropDownView for
     * @param convertView The view to convert into (not used)
     * @param parent The parent of the DropDown view
     * @return The DropDownView corresponding to the item at the specified positoin
     */
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView label = (TextView) inflater.inflate(dropDownResourceId, null);
        label.setText(values[position].toString());

        return label;
    }

}
