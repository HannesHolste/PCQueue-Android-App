package neckbeardhackers.pcqueue.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import neckbeardhackers.pcqueue.event.NetworkConnectionChangeObserver;
import neckbeardhackers.pcqueue.event.NetworkConnectionChangeSubject;

/**
 * Notified when network connectivity changes
 */
public class NetworkStateReceiver extends BroadcastReceiver implements NetworkConnectionChangeSubject {
    /**
     * List of observers
     */
    protected List<NetworkConnectionChangeObserver> observerList;

    /**
     * Boolean that to mark if there is an existing network connection
     */
    protected boolean hasNetwork = true;

    /**
     * Constructor to instantiate the list for the observers
     */
    public NetworkStateReceiver() {
        observerList = new ArrayList<>();
    }

    /**
     * Checks if there is existing network connection on the application
     * @param context Context at which network is being determined
     * @param intent Intent to obtain information about the network connection
     */
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.isConnectedOrConnecting()) {
                hasNetwork = true;

            } else {
                hasNetwork = false;
            }
            notifyObservers(hasNetwork);
        }
    }

    /**
     * Registers a network connection if there is one by adding it to the observer list
     * @param observer Observer to watch for changes in network connection
     */
    @Override
    public void registerNetworkConnectionChangeListener(NetworkConnectionChangeObserver observer) {
        observer.onNetworkConnectivityChange(hasNetwork);
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    /**
     * Notifies the observers of any network changes
     * @param hasConnection Whether or not there is a network connection found
     */
    @Override
    public void notifyObservers(boolean hasConnection) {
        for (NetworkConnectionChangeObserver n : observerList) {
            n.onNetworkConnectivityChange(hasConnection);
        }
    }

    /**
     * Removes a network connection from the observer list
     * @param observer Observer connection to be removed from the list
     */
    @Override
    public void unregisterNetworkConnectionChangeListener(NetworkConnectionChangeObserver observer) {
        observerList.remove(observer);
    }
}