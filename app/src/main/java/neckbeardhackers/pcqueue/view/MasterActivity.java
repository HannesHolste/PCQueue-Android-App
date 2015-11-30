package neckbeardhackers.pcqueue.view;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import neckbeardhackers.pcqueue.R;
import neckbeardhackers.pcqueue.event.NetworkConnectionChangeObserver;
import neckbeardhackers.pcqueue.net.NetworkStateReceiver;

public class MasterActivity extends AppCompatActivity implements NetworkConnectionChangeObserver {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        NetworkStateReceiver receiver = new NetworkStateReceiver();
        registerReceiver(receiver, filter);

        receiver.registerNetworkConnectionChangeListener(this);

    }

    public void onNetworkConnectivityChange(boolean hasConnection) {
        View bar = findViewById(R.id.no_internet_bar);

        if (bar != null) {
            bar.setVisibility(!hasConnection ? View.VISIBLE : View.GONE);
        }
    }
}
