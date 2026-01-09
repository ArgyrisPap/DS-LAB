package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.OrderType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(
    @NotNull @Positive Long personId,
    @NotNull @Positive Long storeId,
    @NotNull @NotEmpty List<OrderItemRequest> orderItemRequestList,
    @NotNull  OrderType type,
    Long existingOrderId

) {
}
