package gr.hua.dit.steetfood.core.service.model;

public record StoreView(
    long id,
    String storeName,
    String storeAddress,
    String phoneNumber
) {
}
