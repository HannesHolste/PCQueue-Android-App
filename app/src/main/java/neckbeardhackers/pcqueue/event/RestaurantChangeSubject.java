package neckbeardhackers.pcqueue.event;


import neckbeardhackers.pcqueue.model.Restaurant;

public interface RestaurantChangeSubject {
     void registerRestaurantChangeListener(RestaurantChangeObserver observer);

    void notifyObservers(Restaurant restaurant);
}
