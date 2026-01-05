package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.StoreType;

public record CreateStoreRequest (
    String storeName,
    String storeAddress,
    StoreType storeType,
    String phoneNumber

) {
}
