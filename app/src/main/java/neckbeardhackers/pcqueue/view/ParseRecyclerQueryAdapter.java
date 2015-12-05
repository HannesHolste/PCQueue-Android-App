package neckbeardhackers.pcqueue.view;

import com.parse.ParseException;
/*
 * The MIT License (MIT)
 *
 * Copyright (c) <2015> <ameron32>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter.QueryFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * NEARLY IDENTICAL REPLACEMENT FOR ParseQueryAdapter ON ListView.
 * REQUIRES THAT YOU SUBCLASS TO CREATE ViewHolder, onBindViewHolder(), and onCreateViewHolder
 * AS ENFORCED BY THE RECYCLERVIEW PATTERN.
 * <p/>
 * TESTED SUCCESSFULLY with RecyclerView v7:21.0.3
 * AND with SuperRecyclerView by Malinskiy
 *
 * @ https://github.com/Malinskiy/SuperRecyclerView
 * SHOULD WORK WITH UltimateRecyclerView
 * <p/>
 * from: https://gist.github.com/ameron32/34329dbd5856bf5ea7c3
 */
public abstract class ParseRecyclerQueryAdapter<T extends ParseObject, U extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<U> {

    private QueryFactory<T> mFactory;
    private final boolean hasStableIds;
    private final List<T> mItems;

    /**
     * Primary Constructor
     * Description: Creates a new query adapter with the provided QueryFactory and keeps track of
     * whether the database IDs are meant to be stable
     * @param factory The QueryFactory that should be set to this adapter
     * @param hasStableIds Whether the database IDs are meant to be stable
     */
    public ParseRecyclerQueryAdapter(final QueryFactory<T> factory, final boolean hasStableIds) {
        mFactory = factory;
        mItems = new ArrayList<T>();
        mDataSetListeners = new ArrayList<OnDataSetChangedListener>();
        mQueryListeners = new ArrayList<OnQueryLoadListener<T>>();
        this.hasStableIds = hasStableIds;

        setHasStableIds(hasStableIds);
        loadObjects();
    }

    protected void setQueryFactory(final QueryFactory<T> factory) {
        mFactory = factory;
    }

    /**
     * Handles the RecyclerView getItemId which simply returns the item ID at the given position if the
     * IDs are stable, and guesses if the IDs are unstable
     * @param position The position of the item ID to get
     * @return The item ID located at position
     */
    @Override
    public long getItemId(int position) {
        if (hasStableIds) {
            return position;
        }

        return super.getItemId(position);
    }

    /**
     * Handles the RecyclerView getItemCount which simply returns the number of items we have in
     * our RecyclerView
     * @return The number of items within our RecyclerView
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Gets the item at a given position
     * @param position The position of the item to get
     * @return The item at the given position
     */
    public T getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Removes the item at a given position within our RecyclerView
     * @param position The position of the item to remove
     */
    public void removeItem(int position) {
        mItems.remove(position);
    }

    /**
     * Adds the given item to the RecyclerView at the given postition, shifting items right if they
     * already exist in that location
     * @param position The position to insert the item at
     * @param toAdd The item to insert
     */
    public void addItem(int position, T toAdd) {
        mItems.add(position, toAdd);
    }

    /**
     * Sets the given item in the given position with the provided item and overwrites it if it
     * already exists
     * @param position The position to put the item in
     * @param item The item to insert into the RecyclerView
     */
    public void setItem(int position, T item) {
        mItems.set(position, item);
    }

    /**
     * Loads the objects from the Adapter's QueryFactory into the RecyclerView list and calls
     * the completion callbacks when finished
     */
    public void loadObjects() {
        dispatchOnLoading();
        final ParseQuery<T> query = mFactory.create();
        query.findInBackground(new FindCallback<T>() {
            ;

            @Override
            public void done(
                    List<T> queriedItems,
                    @Nullable ParseException e) {
                if (e == null) {
                    mItems.clear();
                    mItems.addAll(queriedItems);
                    dispatchOnLoaded(queriedItems, e);
                    notifyDataSetChanged();
                    fireOnDataSetChanged();
                }
            }
        });
    }

    /**
     * The interface for listening to data changes within the RecyclerView
     */
    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

    private final List<OnDataSetChangedListener> mDataSetListeners;

    /**
     * Register a DataSetChangedListener with the RecyclerView so that it is updated every time
     * the RecyclerView object list is changed
     * @param listener The listener to register
     */
    public void addOnDataSetChangedListener(OnDataSetChangedListener listener) {
        mDataSetListeners.add(listener);
    }

    /**
     * De-Register the specified DataSetChangedListener so that it is no longer updated every
     * time the RecyclerView object list is changed
     * @param listener The listener to unregister
     */
    public void removeOnDataSetChangedListener(OnDataSetChangedListener listener) {
        if (mDataSetListeners.contains(listener))
            mDataSetListeners.remove(listener);
    }

    /**
     * Alert all of the registered DataSetChangedListeners that the data set belonging to this
     * RecyclerView has changed
     */
    protected void fireOnDataSetChanged() {
        for (OnDataSetChangedListener registeredListener : mDataSetListeners)
        registeredListener.onDataSetChanged();
    }

    /**
     * The interface for listeners that may register for query change alerts within this adapter
     * @param <T> The type of objects the query would be retrieving
     */
    public interface OnQueryLoadListener<T> {

        public void onLoaded(
                List<T> objects, Exception e);

        public void onLoading();
    }

    private final List<OnQueryLoadListener<T>> mQueryListeners;

    /**
     * Registers a OnQueryLoadListener with the adapter so that it is notified every time a
     * query is loaded
     * @param listener The listener to register
     */
    public void addOnQueryLoadListener( OnQueryLoadListener<T> listener) {
        if (!(mQueryListeners.contains(listener))) {
            mQueryListeners.add(listener);
        }
    }

    /**
     * De-Regsiters an OnQueryLoadListener from the adapter so that it is not notified every time
     * a query is loaded
     * @param listener The listener to de-register
     */
    public void removeOnQueryLoadListener( OnQueryLoadListener<T> listener) {
        if (mQueryListeners.contains(listener)) {
            mQueryListeners.remove(listener);
        }
    }

    /**
     * Alert all registered OnQueryLoadListeners that the query has loaded so that they may
     * react as they please
     */
    private void dispatchOnLoading() {
        for (OnQueryLoadListener<T> l : mQueryListeners) {
            l.onLoading();
        }
    }

    /**
     * Alert all registered OnQueryLoadListeners that the query has completed loading and provide
     * them with the results and error code so that they may react as they please
     * @param objects The results of the query
     * @param e The error code belonging to the query
     */
    private void dispatchOnLoaded(List<T> objects, ParseException e) {
        for (OnQueryLoadListener<T> l : mQueryListeners) {
            l.onLoaded(objects, e);
        }
    }
}