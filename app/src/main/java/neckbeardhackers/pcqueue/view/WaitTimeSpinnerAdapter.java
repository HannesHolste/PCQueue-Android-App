package neckbeardhackers.pcqueue.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import neckbeardhackers.pcqueue.model.WaitTime;

/**
 * Created by hannes on 11/8/15.
 */
public class WaitTimeSpinnerAdapter extends ArrayAdapter<WaitTime> {

    private Context context;

    private WaitTime[] values;
    private int textViewResourceId;
    private int dropDownResourceId;

    public WaitTimeSpinnerAdapter(Context context, int textViewResourceId,
                                  WaitTime[] values) {
        super(context, textViewResourceId, values);
        this.textViewResourceId = textViewResourceId;
        this.dropDownResourceId = textViewResourceId;
        this.context = context;
        this.values = values;
    }

    public int getCount() {
        return values.length;
    }

    public WaitTime getItem(int position) {
        return values[position];
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
        this.dropDownResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView label = (TextView) inflater.inflate(textViewResourceId, null);

        label.setText(values[position].toString());

        return label;
    }


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
