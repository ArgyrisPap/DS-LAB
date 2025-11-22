package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.model.FoodItem;

public interface StoreService {

    boolean updatePrice (double price, FoodItem foodItem);

    void addFoodItem(FoodItem foodItem);
}
