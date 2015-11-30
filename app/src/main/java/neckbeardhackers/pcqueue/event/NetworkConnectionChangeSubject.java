package neckbeardhackers.pcqueue.event;


import neckbeardhackers.pcqueue.model.Restaurant;

public interface NetworkConnectionChangeSubject {
    void registerNetworkConnectionChangeListener(NetworkConnectionChangeObserver observer);
    void notifyObservers(boolean hasConnection);
    void unregisterNetworkConnectionChangeListener(NetworkConnectionChangeObserver observer);
}
