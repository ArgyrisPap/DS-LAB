package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.repository.OrderRepository;
import gr.hua.dit.steetfood.core.service.OrderDataService;

import gr.hua.dit.steetfood.core.service.mapper.OrderMapper;

import gr.hua.dit.steetfood.core.service.model.OrderView;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link OrderDataService}.
 */
@Service
public class OrderDataServiceImpl implements OrderDataService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderDataServiceImpl(final OrderRepository orderRepository,
                                 final OrderMapper orderMapper) {
        if (orderRepository == null) throw new NullPointerException();
        if (orderMapper == null) throw new NullPointerException();
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<OrderView> getAllOrders() {
        final List<Order> ticketList = this.orderRepository.findAll();
        final List<OrderView> ticketViewList = ticketList
            .stream()
            .map(this.orderMapper::convertOrderToOrderView)
            .toList();
        return ticketViewList;
    }

}
