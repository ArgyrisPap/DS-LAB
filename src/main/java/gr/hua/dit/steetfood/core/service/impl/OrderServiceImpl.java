package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.FoodItem;
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
    private final EmailService emailService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            PersonRepository personRepository,
                            StoreRepository storeRepository,
                            CurrentUserProvider currentUserProvider,
                            OrderMapper orderMapper,
                            FoodItemMapper foodItemMapper,
                            EmailService emailService) {
        if (orderRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (storeRepository == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        if (orderMapper == null) throw new NullPointerException();
        if (foodItemMapper == null) throw new NullPointerException();
        if  (emailService == null) throw new NullPointerException();

        this.currentUserProvider = currentUserProvider;
        this.orderMapper = orderMapper;
        this.storeRepository = storeRepository;
        this.personRepository = personRepository;
        this.orderRepository = orderRepository;
        this.foodItemMapper = foodItemMapper;
        this.emailService = emailService;
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

        if (person.getType() != PersonType.USER){ //TODO NA TO KANV USER 'h CLIENT
            throw new IllegalArgumentException("person type must be STUDENT");
        }

        // Security
        //---------------------

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.USER) {
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

        List <Order> orderList= new ArrayList<>();
        if (currentUser.type() == PersonType.USER) {
            orderList = this.orderRepository.findAllByPersonId(currentUser.id());
        }else if(currentUser.type() == PersonType.OWNER){
            //BRISKW POIO MAGAZI EXEI O TEACHER
            //Store store = this.storeRepository.findByOwnerId(currentUser.id()).orElse(null); //TODO BUG! AN EXEI POLLA STORE POIO THA MOY GURISEI??
            //den uelv na rijw exception dioti den einai prog. sfalma, apla o Owner den exei kapoio magazi (to epitrepoyme)
            //if (store == null){return new ArrayList<>();}   //apla toy gyrizw adeia lista
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

            //orderList = this.orderRepository.findAllByStoreId(store.getId());
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

    @Override
    public Long changeOrder(Long orderId) {
        //Checks order status, delete entire order, then controller redirects to createoder form
        if (orderId == null) throw new NullPointerException();
        if (orderId <= 0) throw new IllegalArgumentException();

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

        final Order order;
        try {
            order = this.orderRepository.getReferenceById(orderId);
        } catch (EntityNotFoundException ignored) {
            throw new RuntimeException("Order ID " + orderId + " not found");
        }

        final long orderPersonId;
        if (currentUser.type() == PersonType.USER) {
            orderPersonId = order.getPerson().getId();
        }else {
            throw new SecurityException("unsupported PersonType");
        }
        if (currentUser.id() != orderPersonId) {
            throw new RuntimeException("this Order does not exist for this user."); //
        }
        if (order.getStatus().equals(OrderStatus.SENT_AT) ||
            order.getStatus().equals(OrderStatus.IN_PROCESS) ){

            this.orderRepository.deleteById(orderId); //TODO DEN EINAI SWSTO GIATI SBHNEI THN PALIA
            this.orderRepository.save(order);
            return order.getStore().getId();
        }else {

            throw new RuntimeException("Your order status is: " + order.getStatus()
        +". You can't change your order!");

        }
    }
    @Override
    public OrderView startOrder(@Valid final StartOrderRequest startOrderRequest) {
        if (startOrderRequest == null) throw new NullPointerException();

        Long orderId = startOrderRequest.id();
        final Order order =this.orderRepository.findOrderById(orderId).orElseThrow(
            () -> new IllegalArgumentException("Order ID " + orderId + " not found"));

        // Find if this order matches this owner (current user)
        final long storeId = order.getStore().getId();
        Store store = order.getStore();
        if (store == null) {throw new NullPointerException();} //pleonasmos - den tha einai null logw @NotNull se Order
        Person owner = store.getOwner();
        if (owner == null) {throw new NullPointerException();}//pleonasmos - den tha einai null logw @NotNull se Order


        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

        //security
        if (currentUser.type() != PersonType.OWNER) {throw new
            SecurityException("unsupported PersonType. Only owner/store can start order!");}

        if (currentUser.id() != owner.getId()) {
            //throw new SecurityException ("This order id: "+order.getId() +" is not for your store!" );
            throw new SecurityException ("You are not the owner of this oder's store!" );
        }


        //RULES--------------------------------------------
        if (order.getStatus() != OrderStatus.SENT_AT) {
            throw new IllegalArgumentException("Only sent at order status can be started");
        }
        //-------------------
        order.setStatus(OrderStatus.IN_PROCESS);
        order.setInProgressAt(Instant.now());

        // update the order

        final Order savedOrder = this.orderRepository.save(order);


        final OrderView orderView = this.orderMapper.convertOrderToOrderView(savedOrder);

        //TODO EMAIL NOTIFICATION FOR CHANGE ORDER STATUS
        //find person's email who ordered
        //final String to = order.getPerson().getEmailAddress();
        String to = "arg.papa97@gmail.com"; //testing!
        if (to == null) {throw new IllegalArgumentException();} // @NOT NULL IN ENTITY PERSON!


        this.emailService.sendOrderStartEmail(to, orderId);
        return orderView;
    }
}
