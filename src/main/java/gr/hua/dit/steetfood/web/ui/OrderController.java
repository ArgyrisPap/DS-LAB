package gr.hua.dit.steetfood.web.ui;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.model.OrderStatus;
import gr.hua.dit.steetfood.core.model.OrderType;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.port.impl.dto.RouteInfo;
import gr.hua.dit.steetfood.core.security.CurrentUserProvider;
import gr.hua.dit.steetfood.core.service.OrderService;

import gr.hua.dit.steetfood.core.service.StoreService;
import gr.hua.dit.steetfood.core.service.model.CreateOrderFormReq;
import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;

import gr.hua.dit.steetfood.core.service.model.CreateOrderResult;

import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;

import gr.hua.dit.steetfood.core.service.model.OrderView;
import gr.hua.dit.steetfood.core.service.model.StartOrderRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final StoreService storeService;
    private final CurrentUserProvider  currentUserProvider;

    public OrderController(final StoreService storeService,final OrderService orderService,
                           final CurrentUserProvider currentUserProvider) {
        this.orderService = orderService;
        this.storeService = storeService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/store/{id}/menu")
    public String showStoreMenu(@PathVariable Long id, Model model) {
        Store store = storeService.getStoreById(id).orElse(null);
        if (store == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Store not found");
            //model.addAttribute("errorMessage", "Store not found");
            //return "redirect:/login";
        }

        model.addAttribute("store", store);
        List<FoodItem> menuItems = store.getFoodItemList();

        if (menuItems == null) {
            menuItems = new ArrayList<>();  // Κενή λίστα αν δεν υπάρχει
        }
        model.addAttribute("menuItems", menuItems);

        return "storemenu";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/store/{id}/createorder")
    public String showCreateOrderPage(@PathVariable Long id, Model model,
        Authentication authentication) {
        LOGGER.info("Opening create order page");
        if (!AuthUtils.isAuthenticated(authentication)) {
            LOGGER.warn ("REDIRECTING UNAUTHORIZED TO STORE MENU");
            return "redirect:/store/"+id+"/menu";
        }
        Long storeId = id;

        LOGGER.info("store id is {}", storeId);
        long personId =this.currentUserProvider.requiredStudentId();
        final Store store = storeService.getStoreById(storeId).orElse(null);
        if (store == null){
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Store not found");
        }
        if (!store.isOpen()){
            LOGGER.warn ("REDIRECTING FOR: CLOSED STORE TO STORE MENU ONLY");
            return "redirect:/store/"+id+"/menu";
        }
        final List<FoodItem> menuItems = this.storeService.getFoodItemListByStoreId(storeId);
        if (menuItems.isEmpty()){
            LOGGER.warn("Store {} has no menu items", storeId);
            model.addAttribute("errorMessage", "This store has no available items at the moment");
            return "showstores";
        }

        //TODO AUTHENTICATION
        // Initialize empty form
        final CreateOrderFormReq orderFormRequest = new CreateOrderFormReq(storeId, new ArrayList<Long> (), new ArrayList<Integer> (), OrderType.DELIVERY );
        model.addAttribute("store", store);
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("orderFormRequest", orderFormRequest);

        return "createorder";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/store/{id}/createorder")
    public String handleCreateOrder(@PathVariable final Long id,
        @ModelAttribute("orderFormRequest") @Valid final CreateOrderFormReq orderFormRequest,
        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()){return "createorder";}

        LOGGER.info("=== CREATE ORDER REQUEST ===");
        LOGGER.info("personId: {}", this.currentUserProvider.requiredStudentId());
        LOGGER.info("storeId: {}", orderFormRequest.storeId());
        LOGGER.info("foodItemIds: {}", orderFormRequest.foodItemIds());
        LOGGER.info("quantities: {}", orderFormRequest.quantities());


        Long storeId = id;

        // Convert to domain request
        List<OrderItemRequest> orderItemRequestList = this.orderService
            .convertToOrderItemRequestList(orderFormRequest.foodItemIds(),
                orderFormRequest.quantities());

        orderItemRequestList = orderItemRequestList.stream()
            .filter(oi -> oi.quantity() > 0)
            .toList();

        final CreateOrderRequest createOrderRequest= new CreateOrderRequest(
            this.currentUserProvider.requiredStudentId(),
            orderFormRequest.storeId(),
            orderItemRequestList,
            orderFormRequest.type()
        );

        LOGGER.info("Converted to CreateOrderRequest with {} items",
            createOrderRequest.orderItemRequestList().size());

        //final CreateOrderResult result = orderService.createOrder(createOrderRequest);
        final OrderView orderView = orderService.createOrder(createOrderRequest);
        return "redirect:/orders/" + orderView.id();

    }
    @GetMapping("/orders")
    public String list(final Model model) {
        final List<OrderView> orderViewList = this.orderService.getOrders();
        model.addAttribute("orders", orderViewList);
        return "orders";
    }

    @GetMapping("orders/{orderId}")
    public String detail(@PathVariable final Long orderId, final Model model) {
        if (orderId == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Order not found");
        }
        final OrderView orderView = this.orderService.getOrder(orderId).orElse(null);
        if (orderView == null) {throw new SecurityException("Order not found");}
        //TODO ROUTE INFO ΝΑ ΓΙΝΕΙ ASYNC
        if (orderView.status() == OrderStatus.IN_PROCESS){
            final RouteInfo routeInfo = this.orderService.findOrderRouteInfo(orderId).orElse(null);
            model.addAttribute("routeInfo",routeInfo);
        }

        model.addAttribute("order", orderView);

        model.addAttribute("completeOrderForm", null); // TODO Set.
        return "order";
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("orders/{orderId}/changeorder")
    public String alterOrder(@PathVariable final Long orderId) {
        Long storeId= this.orderService.changeOrder(orderId);

        return "redirect:/store/"+storeId+"/createorder";
    }
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("orders/{orderId}/start")
    public String handleStartForm(@PathVariable final Long orderId) {
        final StartOrderRequest startOrderRequest = new StartOrderRequest(orderId);
        final OrderView orderView = this.orderService.startOrder(startOrderRequest);
        return "redirect:/orders/" + orderView.id();
    }

}
