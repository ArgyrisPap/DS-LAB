package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;
import gr.hua.dit.steetfood.core.service.model.CreateOrderResult;
import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;
import gr.hua.dit.steetfood.core.service.model.OrderView;

import java.util.List;
import java.util.Optional;


public interface OrderService {

    OrderView createOrder(CreateOrderRequest createOrderRequest);

    List<OrderItemRequest> convertToOrderItemRequestList (List <Long> foodItemIds,
                                                          List<Integer> quantities);

    List <OrderView> getOrders ();

    Optional<OrderView> getOrder (Long orderId);
}
