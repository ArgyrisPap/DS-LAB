package gr.hua.dit.steetfood.core.service.model;


import java.util.List;

public record CreateOrderRequest(
    Long personId,
    Long storeId,
    List<OrderItemRequest> orderItemRequestList

) {
}
