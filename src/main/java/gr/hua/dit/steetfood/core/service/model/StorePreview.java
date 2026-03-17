package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.StoreType;

import java.util.List;

//FOR REST API USAGE!!
public record StorePreview(
    long id,
    String storeName,
    String storeAddress,
    StoreType storeType,
    String phoneNumber,
    boolean open,
    double minOrder
) {
}
