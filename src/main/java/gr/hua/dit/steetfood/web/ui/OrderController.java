package gr.hua.dit.steetfood.web.ui;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.repository.StoreRepository;
import gr.hua.dit.steetfood.core.service.MenuService;
import gr.hua.dit.steetfood.core.service.OrderService;

import gr.hua.dit.steetfood.core.service.StoreService;
import gr.hua.dit.steetfood.core.service.model.CreateOrderFormRequest;
import gr.hua.dit.steetfood.core.service.model.CreateOrderRequest;

import gr.hua.dit.steetfood.core.service.model.CreateOrderResult;
import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final MenuService menuService;
    private final StoreService storeService;

    public OrderController(StoreService storeService,OrderService orderService, MenuService menuService) {
        this.orderService = orderService;
        this.storeService = storeService;
        this.menuService = menuService;
    }

    @GetMapping("/store/{id}/createorder")
    public String showCreateOrderPage(@PathVariable Long id, Model model) {
        LOGGER.info("Opening create order page");

        Long storeId = id;
        LOGGER.info("store id is {}", storeId);
        Long personId = 1L;
        //TODO na mpei logikh: dinw storeId, personId sta service kai mou epistrefoun auta poy uelv
        //TODO AUTHENTICATION
        try {
            final Store store = storeService.getStoreById(storeId);
            final List<FoodItem> menuItems = menuService.getMenuForStore(storeId);

            // Check if store has menu
            if (menuItems == null || menuItems.isEmpty()) {
                LOGGER.warn("Store {} has no menu items", storeId);
                model.addAttribute("errorMessage", "This store has no available items at the moment");
                return "showstores"; // or redirect to stores page
            }

            // Initialize empty form
            final CreateOrderFormRequest orderFormRequest =
                new CreateOrderFormRequest(personId, storeId);

            model.addAttribute("store", store);
            model.addAttribute("menuItems", menuItems);
            model.addAttribute("orderFormRequest", orderFormRequest);

            return "createorder";

        } catch (Exception e) {
            LOGGER.error("Error loading order form", e);
            model.addAttribute("errorMessage", "Unable to load menu. Please try again.");
            return "createorder"; //proswrina
        }

    }

    @PostMapping("/store/{id}/createorder")
    public String handleCreateOrder(@PathVariable final Long id,
        @ModelAttribute("orderFormRequest") final CreateOrderFormRequest orderFormRequest,
        final Model model) {

        LOGGER.info("=== CREATE ORDER REQUEST ===");
        LOGGER.info("personId: {}", orderFormRequest.getPersonId());
        LOGGER.info("storeId: {}", orderFormRequest.getStoreId());
        LOGGER.info("foodItemIds: {}", orderFormRequest.getFoodItemIds());
        LOGGER.info("quantities: {}", orderFormRequest.getQuantities());

        Long storeId = id;
        Long personId = 1L;

        if (orderFormRequest.getFoodItemIds() == null || orderFormRequest.getFoodItemIds().isEmpty()) {
            LOGGER.warn("No food items selected");
            return "login";
        }

        // Validation: Check if any items have quantity > 0
        if (!orderFormRequest.hasValidItems()) {
            LOGGER.warn("No items with quantity > 0");
            return "login";
        }

        // Convert to domain request
        final CreateOrderRequest createOrderRequest = orderFormRequest.toCreateOrderRequest();

        LOGGER.info("Converted to CreateOrderRequest with {} items",
            createOrderRequest.orderItemRequestList().size());

        try {
            final CreateOrderResult result = orderService.createOrder(createOrderRequest);

            LOGGER.info("Order result - created: {}, reason: {}", result.created(), result.reason());

            if (result.created()) {
                LOGGER.info("Order created successfully!");
                // TODO: Redirect to order confirmation page or user orders page
                model.addAttribute("successMessage", "Order created successfully!");
                return "redirect:/login"; // order success
            }

            LOGGER.error(" Order creation failed: {}", result.reason());
            return "redirect:/login"; //order failed

        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error during order creation", e);
            return "redirect:/login"; //order failed
        } catch (Exception e) {
            LOGGER.error("Unexpected error during order creation", e);
            return "redirect:/login"; //order failed
        }
    }


}
