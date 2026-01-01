package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.OrderType;

import java.util.ArrayList;
import java.util.List;
//DTO gia na to perasw sto HTML, epeidh prin eixa
// NESTED CLASS ( LIST<OrderItemRequest> inside CreateOrderRequest)
// kai den to dexete to html

public class CreateOrderFormRequest {
    private Long personId;
    private Long storeId;
    private List<Long> foodItemIds= new ArrayList<>();
    private List<Integer> quantities= new ArrayList<>();

    public CreateOrderFormRequest(Long personId,
                                  Long storeId ){
        this.personId = personId;
        this.storeId = storeId;
        //this.foodItemIds = foodItemIds;
        //this.quantities = quantities;
    }
    public CreateOrderFormRequest() {}

    public CreateOrderRequest toDomainRequest() {
        List<OrderItemRequest> items = new ArrayList<>();
        for (int i = 0; i < foodItemIds.size(); i++) {
            if (quantities.get(i) > 0) {
                items.add(new OrderItemRequest(foodItemIds.get(i), quantities.get(i)));
            }
        }
        return new CreateOrderRequest(personId, storeId, items, OrderType.DELIVERY);
    }
    public CreateOrderRequest toCreateOrderRequest() {
        List<OrderItemRequest> items = new ArrayList<>();

        if (foodItemIds != null && quantities != null) {
            int size = Math.min(foodItemIds.size(), quantities.size());
            for (int i = 0; i < size; i++) {
                int qty = quantities.get(i);
                if (qty > 0) {
                    items.add(new OrderItemRequest(foodItemIds.get(i), qty));
                    System.out.println(foodItemIds.get(i));
                    System.out.println("posotita= "+quantities.get(i));
                }
            }
        }

        return new CreateOrderRequest(personId, storeId, items,OrderType.DELIVERY);
    }
    //ISWS PREPEI NA MPEI STO SERVICE ORDER
    public boolean hasValidItems() {
        if (foodItemIds == null || quantities == null) {
            return false;
        }

        int size = Math.min(foodItemIds.size(), quantities.size());
        for (int i = 0; i < size; i++) {
            if (quantities.get(i) > 0) {
                return true;
            }
        }
        return false;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public List<Long> getFoodItemIds() {
        return foodItemIds;
    }

    public void setFoodItemIds(List<Long> foodItemIds) {
        this.foodItemIds = foodItemIds;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }
}
