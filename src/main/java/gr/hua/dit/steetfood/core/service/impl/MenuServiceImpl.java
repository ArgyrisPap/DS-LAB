package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Menu;
import gr.hua.dit.steetfood.core.repository.MenuRepository;
import gr.hua.dit.steetfood.core.service.MenuService;
import gr.hua.dit.steetfood.core.service.StoreService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final StoreService storeService;

    public MenuServiceImpl(MenuRepository menuRepository, StoreService storeService) {
        this.menuRepository = menuRepository;
        this.storeService = storeService;
    }

    @Override
    public List<FoodItem> getMenuForStore(Long storeId) {
        if (storeId == null)throw new IllegalArgumentException("storeId cannot be null");
        if (storeId<=0 ) throw new IllegalArgumentException("storeId cannot be less than 0");
        Menu menu = menuRepository.findByStoreId(storeId).orElse(null);

        if (menu == null) {
            return new ArrayList<>();
        }

        List<FoodItem> foodItems = menu.getFoodItems();

        return foodItems != null ? foodItems : new ArrayList<>();

    }
}
