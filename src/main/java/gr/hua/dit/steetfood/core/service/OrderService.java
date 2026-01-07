package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.port.impl.dto.RouteInfo;
import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;
import gr.hua.dit.steetfood.core.service.model.CreateOrderResult;
import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;
import gr.hua.dit.steetfood.core.service.model.OrderView;
import gr.hua.dit.steetfood.core.service.model.StartOrderRequest;

import java.util.List;
import java.util.Optional;


public interface OrderService {

    OrderView createOrder(CreateOrderRequest createOrderRequest);

    List<OrderItemRequest> convertToOrderItemRequestList (List <Long> foodItemIds,
                                                          List<Integer> quantities);

    List <OrderView> getOrders ();

    Optional<OrderView> getOrder (Long orderId);

    Long changeOrder (Long orderId);

    OrderView startOrder (StartOrderRequest startOrderRequest);

    OrderView denyOrder (StartOrderRequest startOrderRequest);

    Optional <RouteInfo> findOrderRouteInfo (Long orderId);
}
