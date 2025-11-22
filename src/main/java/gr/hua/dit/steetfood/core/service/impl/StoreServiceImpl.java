package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.FoodCategory;
import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Menu;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.model.StoreType;
import gr.hua.dit.steetfood.core.repository.FoodItemRepository;
import gr.hua.dit.steetfood.core.repository.MenuRepository;
import gr.hua.dit.steetfood.core.repository.StoreRepository;
import gr.hua.dit.steetfood.core.service.InitializationService;
import gr.hua.dit.steetfood.core.service.StoreService;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class StoreServiceImpl implements StoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreServiceImpl.class);
    private final StoreRepository storeRepository;
    private final FoodItemRepository foodItemRepository;
    private final MenuRepository menuRepository;
    private final AtomicBoolean initialized;

    public StoreServiceImpl(StoreRepository storeRepository, FoodItemRepository foodItemRepository, MenuRepository menuRepository) {
        if (storeRepository == null) throw new NullPointerException();
        if (foodItemRepository == null)throw new NullPointerException();
        if (menuRepository == null)throw new NullPointerException();
        this.menuRepository = menuRepository;
        this.storeRepository = storeRepository;
        this.foodItemRepository = foodItemRepository;
        this.initialized = new AtomicBoolean(false);
    }

    @Override
    public void addFoodItem(FoodItem foodItem) {
        this.foodItemRepository.save(foodItem);
    }

    @Override
    public boolean updatePrice(double price, FoodItem foodItem) {


        return false;
    }

    @PostConstruct
    public void initData (){
        final boolean alreadyInitialized = this.initialized.getAndSet(true);
        if (alreadyInitialized) {
            LOGGER.warn("Database initialization skipped: initial data has already been populated.");
            return;
        }
        LOGGER.info("Starting database initialization with initial data...");


        // 1. Store
        Store store1 = new Store();
        store1.setStore_address("akti karaiskaki 40");
        store1.setStore_name("Porto Leone");
        store1.setPhoneNumber("2104654372");
        store1.setStoreType(StoreType.GYROS);
        Store savedStore = storeRepository.save(store1);

        Store store2 = new Store();
        store2.setStore_address("epidauroy 26");
        store2.setStore_name("La scala");
        store2.setPhoneNumber("2104648840");
        store2.setStoreType(StoreType.GYROS);
        Store savedStore2 = storeRepository.save(store2);

        // 2. Menu
        List<FoodItem> foodItems = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setStore(savedStore);
        menu1.setFoodItems(foodItems);
        Menu savedMenu = menuRepository.save(menu1);

        // 3. FoodItems
        FoodItem foodItem1 = new FoodItem();
        foodItem1.setDescription("french fries");
        foodItem1.setPrice(3);
        foodItem1.setCategory(FoodCategory.STARTER);
        foodItem1.setMenu(savedMenu);
        FoodItem savedFoodItem = foodItemRepository.save(foodItem1);

        FoodItem foodItem2 = new FoodItem();
        foodItem2.setDescription("burger");
        foodItem2.setPrice(4);
        foodItem2.setCategory(FoodCategory.MEAT);
        foodItem2.setMenu(savedMenu);
        foodItems.add(foodItem2);
        savedFoodItem= foodItemRepository.save(foodItem2);

        FoodItem foodItem3 = new FoodItem();
        foodItem3.setDescription("ceasers");
        foodItem3.setPrice(7);
        foodItem3.setCategory(FoodCategory.SALAD);
        foodItem3.setMenu(savedMenu);
        foodItems.add(foodItem3);
        savedFoodItem= foodItemRepository.save(foodItem3);


        // 4. Update Menu
        savedMenu.getFoodItems().add(savedFoodItem);
        menuRepository.save(savedMenu);



    }
}
