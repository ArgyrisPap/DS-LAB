package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.service.model.CreateStoreRequest;
import gr.hua.dit.steetfood.core.service.model.CreateStoreResult;

public interface StoreService {

    CreateStoreResult createStore (CreateStoreRequest createStoreRequest);
}
