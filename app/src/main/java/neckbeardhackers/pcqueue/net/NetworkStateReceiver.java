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
    protected List<NetworkConnectionChangeObserver> observerList;

    public NetworkStateReceiver() {
        observerList = new ArrayList<>();
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.isConnectedOrConnecting()) {
                notifyObservers(true);
            } else{ //if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                notifyObservers(false);
            }



        }
    }

    @Override
    public void registerNetworkConnectionChangeListener(NetworkConnectionChangeObserver observer) {
        observerList.add(observer);
    }

    @Override
    public void notifyObservers(boolean hasConnection) {
        for (NetworkConnectionChangeObserver n : observerList) {
            n.onNetworkConnectivityChange(hasConnection);
        }
    }

    @Override
    public void unregisterNetworkConnectionChangeListener(NetworkConnectionChangeObserver observer) {
        observerList.remove(observer);
    }
}