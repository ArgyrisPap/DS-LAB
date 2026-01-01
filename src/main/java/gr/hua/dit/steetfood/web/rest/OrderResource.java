package gr.hua.dit.steetfood.web.rest;


import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.service.OrderDataService;
import gr.hua.dit.steetfood.core.service.model.OrderView;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing {@code Order} resource.
 */
@RestController
@RequestMapping(value = "/api/v1/order", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderResource {

    private final OrderDataService orderDataService;

    public OrderResource(final OrderDataService orderDataService) {
        if (orderDataService == null) throw new NullPointerException();
        this.orderDataService = orderDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<OrderView> orders() {
        final List<OrderView> orderViewList = this.orderDataService.getAllOrders();
        //System.out.println("DELETE ME:"+orderViewList); //DELETE
        return orderViewList;
    }
}
