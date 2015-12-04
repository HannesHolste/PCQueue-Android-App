package neckbeardhackers.pcqueue.view;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.event.NetworkConnectionChangeObserver;
import neckbeardhackers.pcqueue.net.NetworkStateReceiver;

public class MasterActivity extends AppCompatActivity implements NetworkConnectionChangeObserver {
    protected NetworkStateReceiver networkConnectionReceiver = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerNetworkConnectionChangeBroadcastReceiver();

    }

    protected void registerNetworkConnectionChangeBroadcastReceiver() {
        if (networkConnectionReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

            NetworkStateReceiver receiver = new NetworkStateReceiver();
            registerReceiver(receiver, filter);

            networkConnectionReceiver = receiver;
            registerNetworkConnectionListener(this);
        }
    }

    public void registerNetworkConnectionListener(NetworkConnectionChangeObserver o) {
        if (networkConnectionReceiver != null) {
            networkConnectionReceiver.registerNetworkConnectionChangeListener(o);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkConnectionListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerNetworkConnectionListener(this);
    }

    public void onNetworkConnectivityChange(boolean hasConnection) {
        View bar = findViewById(R.id.no_internet_bar);

        if (bar != null) {
            bar.setVisibility(!hasConnection ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(networkConnectionReceiver);
            networkConnectionReceiver = null;
        }
        catch (IllegalArgumentException e) {
            // Do nothing. This is expected sometimes
        }
        super.onStop();
    }


}
