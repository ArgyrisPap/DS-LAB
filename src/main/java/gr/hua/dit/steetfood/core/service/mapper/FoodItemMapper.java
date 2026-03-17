package gr.hua.dit.steetfood.core.service.mapper;


import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.service.model.FoodItemView;
import gr.hua.dit.steetfood.core.service.model.OrderItemRequest;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FoodItemMapper {

    public List<OrderItemRequest> convert (List <Long> foodItemIds,
                                        List<Integer> quantities){
        if (foodItemIds == null || foodItemIds.isEmpty()){return null;}
        if (quantities == null || quantities.isEmpty()){return null;}
        if (foodItemIds.size() != quantities.size()){return null;}

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        for (int i=0;i<foodItemIds.size();i++){
            orderItemRequestList.add(new  OrderItemRequest(foodItemIds.get(i),
                quantities.get(i)));
        }
        return orderItemRequestList;
    }

    public List <FoodItemView> convertFoodItemListToViewList (
        List <FoodItem> foodItemList
    ){
        if (foodItemList == null ) throw new NullPointerException("foodItemList is null");
        List <FoodItemView> foodItemViewList = new ArrayList<>();
        for (FoodItem foodItem : foodItemList){
            foodItemViewList.add(new FoodItemView(foodItem.getId(),
                foodItem.getDescription(),foodItem.getPrice(),
                foodItem.getCategory()));
        }
        return foodItemViewList;
    }


}
