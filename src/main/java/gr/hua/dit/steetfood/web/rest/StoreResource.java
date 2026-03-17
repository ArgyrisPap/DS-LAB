package gr.hua.dit.steetfood.web.rest;


import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.model.StoreType;
import gr.hua.dit.steetfood.core.service.StoreService;

import gr.hua.dit.steetfood.core.service.mapper.FoodItemMapper;
import gr.hua.dit.steetfood.core.service.mapper.StoreMapper;
import gr.hua.dit.steetfood.core.service.model.FoodItemView;
import gr.hua.dit.steetfood.core.service.model.StorePreview;
import gr.hua.dit.steetfood.core.service.model.StoreView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@code Store} resource.
 */
@RestController
@RequestMapping(value = "/api/v1/store", produces = MediaType.APPLICATION_JSON_VALUE)
public class StoreResource {
    private final Logger LOGGER = LoggerFactory.getLogger(StoreResource.class);
    private final StoreService storeService;
    private final StoreMapper storeMapper;
    private final FoodItemMapper foodItemMapper;

    public StoreResource (final StoreService storeService,
                          final StoreMapper storeMapper,
                          final FoodItemMapper foodItemMapper){
        if (storeService == null) throw new NullPointerException();
        if (storeMapper == null) throw new NullPointerException();
        if (foodItemMapper == null) throw new NullPointerException();
        this.storeService = storeService;
        this.storeMapper = storeMapper;
        this.foodItemMapper = foodItemMapper;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/search")
    public List<StorePreview> getStorePreviews (@RequestParam(name = "filter", required = false) String filter){
        //StorePreview == StoreView without the foodItems!!
        //System.out.println("Current Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        System.out.println("MPHKA STO GETSTORES");
        if (filter == null || filter.isBlank()) {
            System.out.println("TO FILTRO EINAI KENO!");
            return this.storeMapper.convertStoresToStorePreview(this.storeService.getAllStores());
        }
        try {
            StoreType type = StoreType.valueOf(filter.toUpperCase());
            return this.storeMapper.convertStoresToStorePreview(this.storeService.findStoresByType(type));
        }catch (IllegalArgumentException e){
            //filter given is not a valid StoreType
            LOGGER.warn("FILTER GIVEN( "+filter+" )IS NOT A VALID STORETYPE!");
            String availableTypes = Arrays.toString(StoreType.values());
            LOGGER.warn ("Available StoreTypes are:" + Arrays.toString(StoreType.values()));
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid StoreType: '" + filter + "'. Available types are: " + availableTypes
            );
            //return Collections.emptyList();
        }

    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/{storeId}/menu")
    public List<FoodItemView> getStoreMenu (@PathVariable final Long storeId){

        List <FoodItem> foodItems =this.storeService.getFoodItemListByStoreId(storeId);
        if (foodItems.isEmpty()){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Store with id "+storeId+" has no items");
        }else {
            List <FoodItemView> foodItemViewList = this.foodItemMapper
                .convertFoodItemListToViewList(foodItems);
            return foodItemViewList;
        }
    }

}
