package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.model.StoreType;
import gr.hua.dit.steetfood.core.service.model.CreateStoreRequest;
import gr.hua.dit.steetfood.core.service.model.CreateStoreResult;

import java.util.List;
import java.util.Optional;

public interface StoreService {

    CreateStoreResult createStore (CreateStoreRequest createStoreRequest);

    List <Store> getAllStores();

    List <Store> findStoresByType(StoreType type);

    Optional<Store> getStoreById(Long id);

    List <FoodItem> getFoodItemListByStoreId(Long storeId);

    List <Store> findMyStores ();

    Optional <Store> isOwnerOfStore (Long storeId);

    void changeStoreStatus(Store store);


}
