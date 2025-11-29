package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Menu;
import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.model.OrderItem;
import gr.hua.dit.steetfood.core.model.OrderStatus;
import gr.hua.dit.steetfood.core.model.OrderType;
import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.repository.OrderRepository;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.repository.StoreRepository;
import gr.hua.dit.steetfood.core.security.CurrentUser;
import gr.hua.dit.steetfood.core.security.CurrentUserProvider;
import gr.hua.dit.steetfood.core.service.OrderService;

import gr.hua.dit.steetfood.core.service.mapper.FoodItemMapper;
import gr.hua.dit.steetfood.core.service.mapper.OrderMapper;
import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;

import gr.hua.dit.steetfood.core.service.model.CreateOrderResult;

import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;

import gr.hua.dit.steetfood.core.service.model.OrderView;
import gr.hua.dit.steetfood.web.ui.OrderController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final CurrentUserProvider currentUserProvider;
    private final OrderMapper orderMapper;
    private final FoodItemMapper foodItemMapper;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final StoreRepository storeRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            PersonRepository personRepository,
                            StoreRepository storeRepository,
                            CurrentUserProvider currentUserProvider,
                            OrderMapper orderMapper,
                            FoodItemMapper foodItemMapper) {
        if (orderRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (storeRepository == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        if (orderMapper == null) throw new NullPointerException();
        if (foodItemMapper == null) throw new NullPointerException();
        this.currentUserProvider = currentUserProvider;
        this.orderMapper = orderMapper;
        this.storeRepository = storeRepository;
        this.personRepository = personRepository;
        this.orderRepository = orderRepository;
        this.foodItemMapper = foodItemMapper;
    }

    @Override
    public OrderView createOrder(@Valid final CreateOrderRequest createOrderRequest) {
        //Checks
        if (createOrderRequest == null) throw new NullPointerException();

        //Unpack------------
        final long personId= createOrderRequest.personId();
        final long storeId= createOrderRequest.storeId();
        final List<OrderItemRequest> orderItemsRequest = createOrderRequest.orderItemRequestList();

        //--------------------- GET OBJECTS REQUIRED FOR NEW ORDER
        final Person person = this.personRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("person not found"));

        final Store store = this.storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("store not found"));

        List <OrderItem> orderItems = new ArrayList<>();



        //---------------------

        if (person.getType() != PersonType.STUDENT){ //TODO NA TO KANV USER 'h CLIENT
            throw new IllegalArgumentException("person type must be STUDENT");
        }

        // Security
        //---------------------

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.STUDENT) {
            throw new SecurityException("Student type/role required");
        }
        if (currentUser.id() != personId) {
            throw new SecurityException("Authenticated student does not match the ticket's studentId");
        }

        // Rules
        //---------------------
        //Store cannot be closed
        if (!store.isOpen()) throw new RuntimeException("Store is not OPEN!");//TODO ELAXISTH PARAGGELIA!!!!


        //----------------------------------

        Order order = new Order();
        order.setId(null);
        order.setPerson(person);
        order.setStore(store);
        order.setOrderItems(orderItems);
        order.setCreationDate(Instant.now());
        order.setStatus(OrderStatus.SENT_AT);
        order.setType(OrderType.DELIVERY);//TODO EINAI PROSVRINO GIATI TO EXV NULL, 'h BGAZV TO NULL 'h SKEFTOMAI KATI ALLO
        //TODO BUG!!!!!! PREPEI OPVSDHPOTE NA TO ALLAJV GIATI MPOREI O XRHSTHS NA DWSEI PARAGGELIA

        //----CONVERT orderItemRequest list to orderItem list
        //BRISKW EAN TA ANTIKEIMENA POY EBALE O XRHSTHS YPARXOYN STO MENU TOY KATASTATHMATOS POY EBALE

        List <FoodItem> foodItemsMenu = store.getFoodItemList();
        //OrderView orderView;
        if (foodItemsMenu == null) throw new NullPointerException("this store does not have food items");

        int itemIdQ,i=0;

        for (OrderItemRequest req : orderItemsRequest) {
            Long itemIdReq = req.foodItemId();
            itemIdQ = req.quantity();
            /* ---CHECKS ALREADY HAPPENED----
            if (itemIdReq == null || itemIdReq < 0) throw new NullPointerException();
            if (itemIdQ <= 0) return CreateOrderResult.fail("Quantity must be greater than zero");
            */
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
            orderItem.setPriceAtOrder(foodItem.getPrice() * itemIdQ); //TO DO LAUOS YPOLOGISMOS
            //PROSWRINO!!!!!!!!
            orderItems.add(orderItem); //TODO NA MHN TO KANV ME .ADD
        }
        order.setOrderItems(orderItems); //updated (not sure if needed)
        order = this.orderRepository.save(order);

        final OrderView orderView = this.orderMapper.convertOrderToOrderView(order);
        //TODO LOGIKH GIA NOTIFY XRHSTH ME MAIL GIA THN PARAGGELIA TOY!
        //return orderView;
        return orderView;

    }
    @Override
    public List<OrderItemRequest> convertToOrderItemRequestList (List <Long> foodItemIds,
                                           List<Integer> quantities){
        return this.foodItemMapper.convert(foodItemIds, quantities);
    }

    @Override
    public List<OrderView> getOrders() {
        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        LOGGER.info("ENTERED .GET ORDERS FROM SERVICE");
        List <Order> orderList;
        if (currentUser.type() == PersonType.STUDENT) {
            orderList = this.orderRepository.findAllByPersonId(currentUser.id());
        }else {
            throw new SecurityException("unsupported PersonType"); //MONO OI STUDENTS UELV
        }
        LOGGER.info("ALMOST EXITED .GET ORDERS FROM SERVICE");
        return orderList.stream()
            .map(this.orderMapper::convertOrderToOrderView)
            .toList();
    }

    @Override
    public Optional<OrderView> getOrder(Long orderId) {
        if (orderId == null) throw new NullPointerException();
        if (orderId <= 0) throw new IllegalArgumentException();

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

        final Order order;
        try {
            order = this.orderRepository.getReferenceById(orderId);
        } catch (EntityNotFoundException ignored) {
            return Optional.empty();
        }

        final long orderPersonId;
        if (currentUser.type() == PersonType.STUDENT) {
            orderPersonId = order.getPerson().getId();
        }else {
            throw new SecurityException("unsupported PersonType");
        }
        if (currentUser.id() != orderPersonId) {
            return Optional.empty(); // this Order does not exist for this user.
        }

        final OrderView orderView = this.orderMapper.convertOrderToOrderView(order);


        return Optional.of(orderView);
    }
}
