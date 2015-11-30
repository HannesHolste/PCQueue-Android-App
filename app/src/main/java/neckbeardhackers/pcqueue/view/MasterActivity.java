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
    protected static NetworkStateReceiver networkConnectionReceiver = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerNetworkConnectionChangeBroadcastReceiver();

    }

    protected void registerNetworkConnectionChangeBroadcastReceiver() {
        if (networkConnectionReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

            NetworkStateReceiver receiver = new NetworkStateReceiver();
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            registerReceiver(receiver, filter);

            networkConnectionReceiver = receiver;
            registerNetworkConnectionListener(this);
        }
    }

    public static void registerNetworkConnectionListener(NetworkConnectionChangeObserver o) {
        if (networkConnectionReceiver != null) {
            networkConnectionReceiver.registerNetworkConnectionChangeListener(o);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkConnectionListener(this);
    }

    public void onNetworkConnectivityChange(boolean hasConnection) {
        View bar = findViewById(R.id.no_internet_bar);

        if (bar != null) {
            bar.setVisibility(!hasConnection ? View.VISIBLE : View.GONE);
        }
    }
}
