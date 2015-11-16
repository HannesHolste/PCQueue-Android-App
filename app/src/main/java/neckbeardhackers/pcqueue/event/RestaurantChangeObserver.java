package neckbeardhackers.pcqueue.event;

import java.util.Observable;
import java.util.Observer;

import neckbeardhackers.pcqueue.model.Restaurant;

public interface RestaurantChangeObserver {
    void update(Restaurant updatedRestaurant);
}
