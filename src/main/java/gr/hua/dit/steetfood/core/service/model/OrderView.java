package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.OrderStatus;
import gr.hua.dit.steetfood.core.model.OrderType;

import java.time.Instant;
import java.util.List;

public record OrderView(
    long id,
    PersonView client,
    StoreView store,
    List<String> orderItemDescriptions,
    List <Integer> orderItemQuantites,
    List <Double> orderItemPrices,  //Total Prices of OrderItem. NOT THE PRICE OF INDIVIDUAL FOODITEM!!!
    Instant creatingDate,
    OrderStatus status,
    OrderType type




) {
}
