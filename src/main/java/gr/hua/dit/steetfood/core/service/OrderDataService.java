package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.service.model.OrderView;

import java.util.List;
/**
 * Service for managing {@code Order} for data analytics purposes.
 */
public interface OrderDataService {
    List<OrderView> getAllOrders();
}
