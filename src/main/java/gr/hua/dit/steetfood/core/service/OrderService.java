package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;
import gr.hua.dit.steetfood.core.service.model.CreateOrderResult;


public interface OrderService {

    CreateOrderResult createOrder(CreateOrderRequest createOrderRequest);
}
