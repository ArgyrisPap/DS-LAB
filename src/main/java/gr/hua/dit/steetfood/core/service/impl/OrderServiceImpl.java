package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Menu;
import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.model.OrderItem;
import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.repository.MenuRepository;
import gr.hua.dit.steetfood.core.repository.OrderRepository;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.repository.StoreRepository;
import gr.hua.dit.steetfood.core.service.OrderService;

import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;

import gr.hua.dit.steetfood.core.service.model.CreateOrderResult;

import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final PersonRepository personRepository;
    private final StoreRepository storeRepository;

    public OrderServiceImpl(OrderRepository orderRepository, MenuRepository menuRepository,
                            PersonRepository personRepository, StoreRepository storeRepository) {
        if (orderRepository == null) throw new NullPointerException();
        if (menuRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (storeRepository == null) throw new NullPointerException();
        this.storeRepository = storeRepository;
        this.personRepository = personRepository;
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public CreateOrderResult createOrder(CreateOrderRequest createOrderRequest) {
        if (createOrderRequest == null) throw new NullPointerException();
        if (!personRepository.existsById(createOrderRequest.personId())){
            throw new IllegalArgumentException("person id not found in database");
        }
        if (!storeRepository.existsById(createOrderRequest.storeId())){
            throw new IllegalArgumentException("store id not found  in database");
        }

        final Person person = personRepository.findById(createOrderRequest.personId()).orElse(null);
        final Store store = storeRepository.findById(createOrderRequest.storeId()).orElse(null);

        final List<OrderItemRequest> orderItemsRequest = createOrderRequest.orderItemRequestList();

        if (orderItemsRequest == null) throw new NullPointerException();
        if (orderItemsRequest.isEmpty()) {
            return CreateOrderResult.fail("List of Items to buy is empty");
        }
        //TODO AUTHENTICATION IF USER IS VISITOR TO .FAIL
        //TODO VALIDATE PHONE - MAYBE IS DOUBLE CHECKED NOT NECESSARY
        if (!store.isOpen()) return  CreateOrderResult.fail("Store is not open"); //TODO NA SIGOUREYTV OTI DEN EINAI POTE NULL

        Order order = new Order();
        order.setId(null);
        order.setPerson(person);
        order.setStore(store);
        List <OrderItem> orderItems = new ArrayList<>();
        order.setOrderItems(orderItems);

        //BRISKW EAN TA ANTIKEIMENA POY EBALE O XRHSTHS YPARXOYN STO MENU TOY KATASTATHMATOS POY EBALE
        if (!this.menuRepository.existsByStoreId(store.getId())) throw new NullPointerException("this store does not have Menu");
        Menu menu = menuRepository.findByStoreId(store.getId()).orElse(null);
        if (menu == null ) throw new NullPointerException("this store does not have Menu");
        List <FoodItem> foodItemsMenu = menu.getFoodItems();
        if (foodItemsMenu == null) throw new NullPointerException("this store does not have food items");
        if (foodItemsMenu.isEmpty()) return CreateOrderResult.fail("Menu is empty");

        int itemIdQ,i=0;

        for (OrderItemRequest req : orderItemsRequest) {
            Long itemIdReq = req.foodItemId();
            itemIdQ = req.quantity();

            if (itemIdReq == null || itemIdReq < 0) throw new NullPointerException();
            if (itemIdQ <= 0) return CreateOrderResult.fail("Quantity must be greater than zero");

            FoodItem foodItem = foodItemsMenu.stream()
                .filter(f -> itemIdReq.equals(f.getId()))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException("Item ID " + itemIdReq + " not found in Store's Menu")
                );

            if (foodItem == null) {
                throw new IllegalArgumentException("Item ID " + itemIdReq + " not found in Store's Menu");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemId(null);
            orderItem.setOrder(order);
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(itemIdQ);
            orderItem.setPriceAtOrder(foodItem.getPrice());

            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems); //updated (not sure if needed)
        order.setCreationDate(Instant.now());
        //ORDER. SET STATUS PENDING


        //TODO NA YPOLOGIZW TO SUNOLIKO AUROISMA GIA NA EPITREPW H OXI ELAXISTH PARAGGELIA

        order = this.orderRepository.save(order);

        return CreateOrderResult.success(createOrderRequest);

    }
}
