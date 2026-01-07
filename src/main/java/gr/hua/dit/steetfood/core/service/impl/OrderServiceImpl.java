package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.model.OrderItem;
import gr.hua.dit.steetfood.core.model.OrderStatus;
import gr.hua.dit.steetfood.core.model.OrderType;
import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.port.AddressPort;
import gr.hua.dit.steetfood.core.port.RoutePort;
import gr.hua.dit.steetfood.core.port.impl.dto.AddressResult;
import gr.hua.dit.steetfood.core.port.impl.dto.RouteInfo;
import gr.hua.dit.steetfood.core.repository.OrderRepository;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.repository.StoreRepository;
import gr.hua.dit.steetfood.core.security.CurrentUser;
import gr.hua.dit.steetfood.core.security.CurrentUserProvider;
import gr.hua.dit.steetfood.core.service.EmailService;
import gr.hua.dit.steetfood.core.service.OrderService;

import gr.hua.dit.steetfood.core.service.mapper.FoodItemMapper;
import gr.hua.dit.steetfood.core.service.mapper.OrderMapper;
import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;

import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;

import gr.hua.dit.steetfood.core.service.model.OrderView;
import gr.hua.dit.steetfood.core.service.model.StartOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final boolean fakeEmail = true; //TODO για να στελνονται τα εμαιλ μονο σε δικο μου email

    private final CurrentUserProvider currentUserProvider;
    private final OrderMapper orderMapper;
    private final FoodItemMapper foodItemMapper;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final StoreRepository storeRepository;
    private final EmailService emailService;
    private final RoutePort routePort;
    private final AddressPort addressPort;

    public OrderServiceImpl(OrderRepository orderRepository,
                            PersonRepository personRepository,
                            StoreRepository storeRepository,
                            CurrentUserProvider currentUserProvider,
                            OrderMapper orderMapper,
                            FoodItemMapper foodItemMapper,
                            EmailService emailService,
                            RoutePort routePort,
                            AddressPort addressPort) {
        if (orderRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (storeRepository == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        if (orderMapper == null) throw new NullPointerException();
        if (foodItemMapper == null) throw new NullPointerException();
        if  (emailService == null) throw new NullPointerException();
        if (routePort == null ) throw new NullPointerException();
        if (addressPort == null) throw new NullPointerException();

        this.currentUserProvider = currentUserProvider;
        this.orderMapper = orderMapper;
        this.storeRepository = storeRepository;
        this.personRepository = personRepository;
        this.orderRepository = orderRepository;
        this.foodItemMapper = foodItemMapper;
        this.emailService = emailService;
        this.routePort= routePort;
        this.addressPort = addressPort;
    }

    @Override
    public OrderView createOrder(@Valid final CreateOrderRequest createOrderRequest) {
        //Checks
        if (createOrderRequest == null) throw new NullPointerException();

        //Unpack------------
        final long personId= createOrderRequest.personId();
        final long storeId= createOrderRequest.storeId();
        final List<OrderItemRequest> orderItemsRequest = createOrderRequest.orderItemRequestList();
        final OrderType orderType = createOrderRequest.type();

        //--------------------- GET OBJECTS REQUIRED FOR NEW ORDER
        final Person person = this.personRepository.findById(personId)
            .orElseThrow(() -> new IllegalArgumentException("person not found"));

        final Store store = this.storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("store not found"));

        //List <OrderItem> orderItems = new ArrayList<>();



        //---------------------

        if (person.getType() != PersonType.USER){
            throw new IllegalArgumentException("person type must be User");
        }

        // Security
        //---------------------

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.USER) {
            throw new SecurityException("User type/role required");
        }
        if (currentUser.id() != personId) {
            throw new SecurityException("Authenticated user does not match the order's userId");
        }

        // Rules
        //---------------------
        //Store cannot be closed
        if (!store.isOpen()) throw new RuntimeException("Store is not OPEN!");



        //----------------------------------
        Order order;
        if (createOrderRequest.existingOrderId() != null) {
            order = orderRepository.findById(createOrderRequest.existingOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            // ΠΡΟΣΟΧΗ: Καθαρίζουμε τα παλιά είδη για να μπουν τα νέα
            order.getOrderItems().clear();
            LOGGER.info("Ordered Items after:"+order.getOrderItems());
        } else {
            order = new Order();
            order.setPerson(person);
            order.setStore(store);
            //order.setOrderItems(orderItems);
            order.setCreationDate(Instant.now());
            order.setId(null);
        }

        order.setStatus(OrderStatus.SENT_AT);
        order.setType(orderType);


        //----CONVERT orderItemRequest list to orderItem list
        //BRISKW EAN TA ANTIKEIMENA POY EBALE O XRHSTHS YPARXOYN STO MENU TOY KATASTATHMATOS POY EBALE

        List <FoodItem> foodItemsMenu = store.getFoodItemList();
        if (foodItemsMenu == null) throw new NullPointerException("this store does not have food items");

        int itemIdQ,i=0;
        double totalPrice=0.0;

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
            orderItem.setPriceAtOrder(foodItem.getPrice() * itemIdQ);
            totalPrice += foodItem.getPrice() * itemIdQ;
            order.getOrderItems().add(orderItem);
            //orderItems.add(orderItem); //TODO NA MHN TO KANV ME .ADD
        }
        LOGGER.info("TOTAL PRICE OF ORDER IS="+totalPrice);
        //=======LAST BUSINESS RULE
        if (orderType == OrderType.DELIVERY){

            if (totalPrice < store.getMinOrder()){
                throw new RuntimeException("Your Order's Total Price does not exceed Store's minimum Order Price");
            }
        }
        //order.setOrderItems(orderItems); //updated (not sure if needed)
        order = this.orderRepository.save(order);

        final OrderView orderView = this.orderMapper.convertOrderToOrderView(order);
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

        List <Order> orderList= new ArrayList<>();
        if (currentUser.type() == PersonType.USER) {
            orderList = this.orderRepository.findAllByPersonId(currentUser.id())
                .stream()
                .filter(order ->
                    order.getStatus() != OrderStatus.DENIED
                        || order.getVisibleUntil() == null
                        || order.getVisibleUntil().isAfter(Instant.now())
                ).toList();

        }else if(currentUser.type() == PersonType.OWNER){
            //BRISKW POIO MAGAZI EXEI O OWNER


            long id = currentUser.id();
            List <Store> stores=this.storeRepository.findStoresByOwnerId(id);
            if (stores == null || stores.isEmpty()){return List.of();}
            List <Long> storeIds= new ArrayList<>();

            for (Store store : stores) {
                //orderList.addAll(this.orderRepository.findAllByStoreId(store.getId()));
                storeIds.add(store.getId());
            }
            for (Long storeId : storeIds) {
                List <Order> ordersPerStore = this.orderRepository.findAllByStoreId(storeId);
                orderList.addAll(ordersPerStore);
            }

        }else {
            throw new SecurityException("unsupported PersonType"); //MONO OI STUDENTS/TEACHERs UELV
        }
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
        if (order.getVisibleUntil()!= null &&
            order.getVisibleUntil().isBefore(Instant.now())) throw new SecurityException("This order does not exist anymore");
        final long orderPersonId;
        if (currentUser.type() == PersonType.USER) {
            orderPersonId = order.getPerson().getId();
        }else if (currentUser.type() == PersonType.OWNER){
            //Briskw to store pou einai linked me to order. Kai briskw to id tou owner
            Store store = order.getStore();
            orderPersonId = store.getOwner().getId();
        }else {
            throw new SecurityException("unsupported PersonType");
        }
        if (currentUser.id() != orderPersonId) {
            return Optional.empty(); // this Order does not exist for this user.
            //OR: this user (owner) does not own the store which has that order!
        }

        final OrderView orderView = this.orderMapper.convertOrderToOrderView(order);


        return Optional.of(orderView);
    }
    public Long changeOrder(Long orderId) {
        CurrentUser currentUser = currentUserProvider.requireCurrentUser();

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPerson().getId() != currentUser.id())
            throw new SecurityException("Unauthorized");

        if (order.getStatus() != OrderStatus.SENT_AT)
            throw new RuntimeException("Order cannot be edited");

        return order.getStore().getId();
    }


    @Override
    public OrderView startOrder(@Valid final StartOrderRequest startOrderRequest) {
        return this.matchesOwner(startOrderRequest, OrderStatus.IN_PROCESS);

    }
    @Override
    public Optional<RouteInfo> findOrderRouteInfo(Long orderId) {
        //find the order
        OrderView orderView = this.getOrder(orderId).orElseThrow();
        if (orderView.type() != OrderType.DELIVERY) {
            LOGGER.warn("BGHKA ME EMPTY ROUTE INFO");
            return Optional.empty();
        }
        //extract store Address
        String storeAddress= orderView.store().storeAddress();
        if (storeAddress == null) return Optional.empty();
        String storeAddressFormated = String.join("+", storeAddress.trim().split("\\s+"));
        AddressResult storeAddressResult = this.addressPort.findAdress(storeAddressFormated).orElseThrow();
        LOGGER.info("STORE ADDRESS!");
        LOGGER.info("LAT="+storeAddressResult.lat()+"\nLON="+storeAddressResult.lon()+"\n=====================");


        //extract person Address
        String personAddress = orderView.client().rawAddress();
        String personAddressFormated = String.join("+", personAddress.trim().split("\\s+"));
        AddressResult personAddressResult = this.addressPort.findAdress(personAddressFormated).orElseThrow();

        LOGGER.info("PERSON ADDRESS!");
        LOGGER.info("LAT="+personAddressResult.lat()+"\nLON="+personAddressResult.lon()+"\n=====================");


        RouteInfo routeInfo = this.routePort.getRoute(storeAddressResult.lat(), storeAddressResult.lon(),personAddressResult.lat(),personAddressResult.lon());

        LOGGER.info("ROUTE INFO!!! DISTANCE="+routeInfo.distance()+" DURATION="+routeInfo.durationMinutes());

        return Optional.of(routeInfo);
    }
    private OrderView matchesOwner (StartOrderRequest startOrderRequest, OrderStatus statusToAchieve){
        if (statusToAchieve == null) throw new NullPointerException();
        if (startOrderRequest == null) throw new NullPointerException();
        if (statusToAchieve != OrderStatus.IN_PROCESS && statusToAchieve != OrderStatus.DENIED) throw new IllegalArgumentException("not yet implemented");


        Long orderId = startOrderRequest.id();
        final Order order =this.orderRepository.findOrderById(orderId).orElseThrow(
            () -> new IllegalArgumentException("Order ID " + orderId + " not found"));

        // Find if this order matches this owner (current user)
        final long storeId = order.getStore().getId();
        Store store = order.getStore();
        if (store == null) {throw new NullPointerException();} //pleonasmos - den tha einai null logw @NotNull se Order
        Person owner = store.getOwner();
        if (owner == null) {throw new NullPointerException();}//pleonasmos - den tha einai null logw @NotNull se Store


        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

        //security
        if (currentUser.type() != PersonType.OWNER) {throw new
            SecurityException("unsupported PersonType. Only owner store can start/deny order!");}

        if (currentUser.id() != owner.getId()) {
            //throw new SecurityException ("This order id: "+order.getId() +" is not for your store!" );
            throw new SecurityException ("You are not the owner of this oder's store!" );
        }

        //RULES--------------------------------------------

        if (statusToAchieve == OrderStatus.IN_PROCESS ) {
            if (order.getStatus() != OrderStatus.SENT_AT) throw new IllegalArgumentException("Only sent at order status can be started");
            order.setStatus(OrderStatus.IN_PROCESS);
            order.setInProgressAt(Instant.now());
        }

        if (statusToAchieve == OrderStatus.DENIED) {
            if (order.getStatus() != OrderStatus.SENT_AT) throw new IllegalArgumentException("Only sent at order status can be denied");
            order.setStatus(OrderStatus.DENIED);
            order.setDeniedAt(Instant.now());
            //TSET METHOD TO SET DELETED STATUS IN 5 MINUTES
            order.setVisibleUntil(Instant.now().plus(2, ChronoUnit.MINUTES));

        }

        //-------------------

        // update the order

        final Order savedOrder = this.orderRepository.save(order);
        final OrderView orderView = this.orderMapper.convertOrderToOrderView(savedOrder);

        //find person's email who ordered
        String to;
        if (fakeEmail){
            to = "arg.papa97@gmail.com"; //testing!
        }else {
            to = order.getPerson().getEmailAddress();
        }

        if (to == null) {throw new IllegalArgumentException();} // @NOT NULL IN ENTITY PERSON!

        if (statusToAchieve == OrderStatus.DENIED){
            this.emailService.sendOrderDeniedEmail(to, orderId);
        }else if (statusToAchieve == OrderStatus.IN_PROCESS){
            this.emailService.sendOrderStartEmail(to, orderId);
        }


        return orderView;
    }

    @Override
    public OrderView denyOrder(@Valid StartOrderRequest startOrderRequest) {
        return this.matchesOwner(startOrderRequest, OrderStatus.DENIED);

    }
}
