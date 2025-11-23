package gr.hua.dit.steetfood.web.ui;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.service.MenuService;
import gr.hua.dit.steetfood.core.service.StoreService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StoreMenuController {

    private final StoreService storeService;
    private final MenuService menuService;

    public StoreMenuController(StoreService storeService, MenuService menuService) {
        this.storeService = storeService;
        this.menuService = menuService;
    }

    @GetMapping("/store/{id}/menu")
    public String showStoreMenu(@PathVariable Long id, Model model) {
        Store store = storeService.getStoreById(id);
        if (store == null) {
            model.addAttribute("errorMessage", "Store not found");
            return "login";
        }

        model.addAttribute("store", store);
        List<FoodItem> menuItems = menuService.getMenuForStore(id);
        if (menuItems == null) {
            menuItems = new ArrayList<>();  // Κενή λίστα αν δεν υπάρχει
        }
        model.addAttribute("menuItems", menuItems);

        return "storemenu";
    }
}
