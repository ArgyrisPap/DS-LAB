package gr.hua.dit.steetfood.core.service.mapper;


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
}
