package neckbeardhackers.pcqueue.event;

public interface NetworkConnectionChangeObserver {
    void onNetworkConnectivityChange(boolean hasConnection);
}
