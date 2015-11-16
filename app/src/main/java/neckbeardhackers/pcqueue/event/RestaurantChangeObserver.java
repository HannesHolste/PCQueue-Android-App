package neckbeardhackers.pcqueue.event;

import neckbeardhackers.pcqueue.model.Restaurant;

public interface RestaurantChangeObserver {
    void update(Restaurant updatedRestaurant);
}
