package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.OrderType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

public record CreateOrderFormReq(
        @NotNull @Positive Long storeId,
        @NotNull @NotEmpty List<Long>foodItemIds,
        @NotNull @NotEmpty List<Integer> quantities,
        @NotNull  OrderType type
) {
}
