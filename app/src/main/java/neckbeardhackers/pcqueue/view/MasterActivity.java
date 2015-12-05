package neckbeardhackers.pcqueue.view;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.event.NetworkConnectionChangeObserver;
import neckbeardhackers.pcqueue.net.NetworkStateReceiver;

/**
 * MasterActivity Class
 * Description: A template activity which is also a NetworkConnectionChangeObserver that has the
 * ability to display a "No Internet Connection" bar at the top of the activity if internet
 * connection is lost. All other activities should extend this activity.
 */
public class MasterActivity extends AppCompatActivity implements NetworkConnectionChangeObserver {
    protected NetworkStateReceiver networkConnectionReceiver = null;

    /**
     * Description: Handles the creation of the activity and registers the activity as a
     * listener on the network state.
     * @param savedInstanceState The previous state of the activity to load from
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerNetworkConnectionChangeReceiver();
    }

    /**
     * Description: Registers the MasterActivity as a listener for network changes. Only proceeds
     * if the activity is not already registered.
     */
    protected void registerNetworkConnectionChangeReceiver() {
        // Do not proceed if the activity is already registered
        if (networkConnectionReceiver != null)
            return;

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        NetworkStateReceiver receiver = new NetworkStateReceiver();
        registerReceiver(receiver, filter);

        networkConnectionReceiver = receiver;
        registerNetworkConnectionListener(this);
    }

    /**
     * Description: Registers the provided listener as a listener on the activity's network state
     * @param observer The listener to register
     */
    public void registerNetworkConnectionListener(NetworkConnectionChangeObserver observer) {
        // Do not register anything if observer is invalid
        if (networkConnectionReceiver == null)
            return;

        networkConnectionReceiver.registerNetworkConnectionChangeListener(observer);
    }

    /**
     * Description: Handle the resuming of this activity (ie if the user switches into this
     * activity via the recent apps screen) and re-registers the activity to listen to network state
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkConnectionChangeReceiver();
    }

    /**
     * Description: Handle the starting of this activity (ie if the user somehow starts the app
     * from this activity) and re-registers the activity to listen to network state
     */
    @Override
    protected void onStart() {
        super.onStart();
        registerNetworkConnectionChangeReceiver();
    }

    /**
     * Description: Handle when the network state changes by displaying the "No Internet" bar on
     * the top of the activity
     * @param hasConnection Whether the network connection is valid
     */
    public void onNetworkConnectivityChange(boolean hasConnection) {
        View bar = findViewById(R.id.no_internet_bar);

        if (bar != null) {
            bar.setVisibility(!hasConnection ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Description: Handle when the activity is paused (ie put into the recent apps screen) by
     * unregistering the activity as a network listener
     */
    public void onPause() {
        unregisterNetworkConnectionChangeReceiver();
        super.onPause();
    }

    /**
     * Description: Handle when activity is stopped (ie closed) by unregistering teh activity as a
     * network listener
     */
    @Override
    public void onStop() {
        unregisterNetworkConnectionChangeReceiver();
        super.onStop();
    }

    /**
     * Description: Unregister the activity as a listener on network changes. If the activity is
     * currently not registered, this does nothing
     */
    protected void unregisterNetworkConnectionChangeReceiver() {
        try {
            unregisterReceiver(networkConnectionReceiver);
            networkConnectionReceiver = null;
        }
        catch (IllegalArgumentException e) {
            // Indicates the activity wasn't registered already (Maybe it was manually
            // de-registered? Anyway, it's no big deal and this error can be ignored
        }
    }


}
