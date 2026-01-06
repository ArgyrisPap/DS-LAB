package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.model.FoodCategory;
import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Person;
import gr.hua.dit.steetfood.core.model.PersonType;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.model.StoreType;
import gr.hua.dit.steetfood.core.repository.FoodItemRepository;
import gr.hua.dit.steetfood.core.repository.PersonRepository;
import gr.hua.dit.steetfood.core.repository.StoreRepository;
import gr.hua.dit.steetfood.core.security.CurrentUser;
import gr.hua.dit.steetfood.core.security.CurrentUserProvider;
import gr.hua.dit.steetfood.core.service.StoreService;
import gr.hua.dit.steetfood.core.service.model.CreateStoreRequest;
import gr.hua.dit.steetfood.core.service.model.CreateStoreResult;
import jakarta.annotation.PostConstruct;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class StoreServiceImpl implements StoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreServiceImpl.class);
    private final StoreRepository storeRepository;
    private final FoodItemRepository foodItemRepository;
    //private final AtomicBoolean initialized;
    private final PersonRepository personRepository; //MONO GIA TESTING STHN EISAGWGH TWN STORES
    private final CurrentUserProvider currentUserProvider;

    public StoreServiceImpl(StoreRepository storeRepository, FoodItemRepository foodItemRepository, PersonRepository personRepository, CurrentUserProvider currentUserProvider) {
        if (storeRepository == null) throw new NullPointerException();
        if (foodItemRepository == null)throw new NullPointerException();
        if (personRepository == null)throw new NullPointerException();
        if (currentUserProvider == null)throw new NullPointerException();
        this.storeRepository = storeRepository;
        this.currentUserProvider = currentUserProvider;
        this.foodItemRepository = foodItemRepository;
        this.personRepository = personRepository;
        //this.initialized = new AtomicBoolean(true); //TODO CREATE-DROP & FIRST TIME UPDATE=FALSE, UPDATE=TRUE
    }

    @Override
    public CreateStoreResult deleteStore(Long id) {
        //TODO ONLY FOR ADMIN OF SITE
        if (!storeRepository.existsById(id)) {
            return CreateStoreResult.fail("Store not found");
        }
        Store store =  storeRepository.findById(id).orElse(null);
        String storeName= store.getStoreName();
        String storeAddress= store.getStoreAddress();
        String phoneNumber= store.getPhoneNumber();
        StoreType storeType= store.getStoreType();
        CreateStoreRequest createStoreRequest = new CreateStoreRequest(storeName,storeAddress,storeType,phoneNumber);

        //TODO : PREPEI PRWTA NA KANV DELETE TOUS ALLOUS PINAKES
        this.storeRepository.deleteById(id);

        return CreateStoreResult.success(createStoreRequest);
    }
    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();

    }

    @Override
    public List<Store> findStoresByType(StoreType type) {
        if (type == null) return new ArrayList<>();
        return this.storeRepository.findStoresByStoreType(type);
    }

    @Override
    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    @Override
    public List<Store> findMyStores() { //TODO NA GINEI LISTA APO STOREVIEW!!!
        final CurrentUser currentUser =this.currentUserProvider.requireCurrentUser();
        Long ownerId = currentUser.id();

        final Person owner = personRepository.findById(ownerId).orElse(null);
        if (owner == null) throw new IllegalArgumentException("Person with id:"+ownerId+" not found");
        if (owner.getType() != PersonType.OWNER){throw new IllegalArgumentException("Person is not OWNER");}

        List <Store> stores;
        try {
            stores = this.storeRepository.findStoresByOwnerId(ownerId);
        }catch (EntityNotFoundException ignored){
            LOGGER.warn("This person does not own any store");
            stores = new ArrayList<>();
        }


        //if (stores == null) throw new IllegalArgumentException("This person does not own any store!");
        return stores;
    }

    @Override
    public List <FoodItem> getFoodItemListByStoreId(Long storeId) {
        final Store store = this.getStoreById(storeId).orElse(null);
        if (store == null) throw new ResponseStatusException
            (HttpStatusCode.valueOf(404), "Store (inside getfoodlist) not found");
        return store.getFoodItemList();
        //return List.copyOf(store.getFoodItemList());   ISWS EINAI KALUTERO
    }

    @Override
    public CreateStoreResult createStore(CreateStoreRequest createStoreRequest) {
        if (createStoreRequest ==null) throw new NullPointerException();

        final String storeAddress = createStoreRequest.storeAddress();
        final String storeName = createStoreRequest.storeName().strip();
        final String storePhoneNumber = createStoreRequest.phoneNumber().strip();
        final StoreType storeType = createStoreRequest.storeType();
    //TODO ONLY FOR ADMIN OF SITE
    //TODO NA SYNEXISV NA KANV TOUS ELEGXOUS GIA TO THLEFWNO
        /*final PhoneNumberValidationResult phoneNumberValidationResult
            = this.phoneNumberPort.validate(mobilePhoneNumber);
        if (!phoneNumberValidationResult.isValidMobile()) {
            return CreatePersonResult.fail("Mobile Phone Number is not valid");
        }
        mobilePhoneNumber = phoneNumberValidationResult.e164();*/
         if (this.storeRepository.existsByStoreAddress(storeAddress)) {
             return CreateStoreResult.fail("Store with address:"+storeAddress+" already exists");
         }
         if (this.storeRepository.existsByPhoneNumber(storePhoneNumber)){
             return CreateStoreResult.fail("Phone number with address:"+storePhoneNumber+" already exists");
         }

         Store store= new Store();
         store.setStoreId(null);
         store.setStoreAddress(storeAddress);
         store.setStoreName(storeName);
         store.setPhoneNumber(storePhoneNumber);
         store.setStoreType(storeType);
         store.setOpen(false); //ALWAYS FALSE AT START

        store= this.storeRepository.save(store);

        return CreateStoreResult.success(createStoreRequest);
    }

    @PostConstruct
    public void initData (){
        //final boolean alreadyInitialized = this.initialized.getAndSet(true);
        if (storeRepository.count() > 0) {
            LOGGER.warn("STORE Database initialization skipped: initial data has already been populated.");
            return;
        }
        LOGGER.info("Starting database (FOR STORES) initialization with initial data...");


        // 1. Store 1
        Store store1 = new Store();
        store1.setStoreAddress("Ακτη Καραϊσκακη 40 Σαλαμινα");
        store1.setStoreName("Porto Leone");
        store1.setPhoneNumber("2104654372");
        store1.setStoreType(StoreType.GYROS);
        store1.setMinOrder(12);
        store1.setOpen(false);//testing
        store1.setOwner(this.personRepository.findByHuaId("t0001").orElse(null));
        Store savedStore = storeRepository.save(store1);

        List<FoodItem> foodItemsList1 = new ArrayList<>();


        FoodItem food1 = new FoodItem();
        food1.setDescription("french fries");
        food1.setPrice(3);
        food1.setCategory(FoodCategory.STARTER);
        food1.setStore(savedStore);
        foodItemsList1.add(food1);
        FoodItem savedFood1= foodItemRepository.save(food1);

        FoodItem food2 = new FoodItem();
        food2.setDescription("burger");
        food2.setPrice(4);
        food2.setCategory(FoodCategory.MEAT);
        food2.setStore(savedStore);
        foodItemsList1.add(food2);
        FoodItem savedFood2= foodItemRepository.save(food2);

        FoodItem food3 = new FoodItem();
        food3.setDescription("ceasers");
        food3.setPrice(7);
        food3.setCategory(FoodCategory.SALAD);
        food3.setStore(savedStore);
        foodItemsList1.add(food3);
        FoodItem savedFood3= foodItemRepository.save(food3);
        store1.setFoodItemList(foodItemsList1);


        //STORE 2========================
        Store store2 = new Store();
        store2.setStoreAddress("Ακτη Καραισκακη 42 Σαλαμινα");
        store2.setStoreName("La scala");
        store2.setPhoneNumber("2104648840");
        store2.setStoreType(StoreType.GYROS);
        store2.setMinOrder(8);
        store2.setOwner(this.personRepository.findByHuaId("t0002").orElse(null));
        Store savedStore2 = storeRepository.save(store2);

        List<FoodItem> foodItemsList2 = new ArrayList<>();

        FoodItem food4 = new FoodItem();
        food4.setDescription("ceasers");
        food4.setPrice(7);
        food4.setCategory(FoodCategory.SALAD);
        food4.setStore(savedStore2);
        foodItemsList2.add(food4);
        FoodItem savedFood4= foodItemRepository.save(food4);

        store2.setFoodItemList(foodItemsList2);

        //STORE 3========================
        Store store3 = new Store();
        store3.setStoreAddress("Επιδαυρου 26 Αιγαλεω");
        store3.setStoreName("Burget Town");
        store3.setPhoneNumber("2104600003");
        store3.setStoreType(StoreType.BURGER);
        store3.setMinOrder(5);
        store3.setOwner(this.personRepository.findByHuaId("t0001").orElse(null));
        Store savedStore3 = storeRepository.save(store3);

        List<FoodItem> foodItemsList3 = new ArrayList<>();

        FoodItem food5 = new FoodItem();
        food5.setDescription("Classic Burger");
        food5.setPrice(9.5);
        food5.setCategory(FoodCategory.MEAT);
        food5.setStore(savedStore3);
        foodItemsList3.add(food5);
        FoodItem savedFood5= foodItemRepository.save(food5);
        store3.setFoodItemList(foodItemsList3);

        //STORE 4========================
        Store store4 = new Store();
        store4.setStoreAddress("Σαλαμινος 24 Πειραιας");
        store4.setStoreName("Pizza Trattoria");
        store4.setPhoneNumber("2104600004");
        store4.setStoreType(StoreType.BURGER);
        store4.setMinOrder(9);
        store4.setOwner(this.personRepository.findByHuaId("t0001").orElse(null));
        Store savedStore4 = storeRepository.save(store4);

        List<FoodItem> foodItemsList4 = new ArrayList<>();

        FoodItem food6 = new FoodItem();
        food6.setDescription("Fish and chips");
        food6.setPrice(12.5);
        food6.setCategory(FoodCategory.FISH);
        food6.setStore(savedStore4);
        foodItemsList4.add(food6);
        FoodItem savedFood6= foodItemRepository.save(food6);
        //store4.setFoodItemList(foodItemsList4);


        FoodItem food7 = new FoodItem();
        food7.setDescription("Smash burger");
        food7.setPrice(9.5);
        food7.setCategory(FoodCategory.MEAT);
        food7.setStore(savedStore4);
        foodItemsList4.add(food7);
        FoodItem savedFood7= foodItemRepository.save(food7);
        //store4.setFoodItemList(foodItemsList4);

        FoodItem food8 = new FoodItem();
        food8.setDescription("tzatziki");
        food8.setPrice(3.25);
        food8.setCategory(FoodCategory.STARTER);
        food8.setStore(savedStore4);
        foodItemsList4.add(food8);
        FoodItem savedFood8= foodItemRepository.save(food8);
        //store4.setFoodItemList(foodItemsList4);


        FoodItem food9 = new FoodItem();
        food9.setDescription("Greek salad");
        food9.setPrice(7.5);
        food9.setCategory(FoodCategory.SALAD);
        food9.setStore(savedStore4);
        foodItemsList4.add(food9);
        FoodItem savedFood9= foodItemRepository.save(food9);
        store4.setFoodItemList(foodItemsList4);


    }
}
