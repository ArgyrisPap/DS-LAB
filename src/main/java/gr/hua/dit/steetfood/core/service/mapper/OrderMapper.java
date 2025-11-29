package gr.hua.dit.steetfood.core.service.mapper;

import gr.hua.dit.steetfood.core.model.Order;
import gr.hua.dit.steetfood.core.model.OrderItem;
import gr.hua.dit.steetfood.core.service.model.OrderView;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper to convert {@link Order} to {@link gr.hua.dit.steetfood.core.service.model.OrderView}.
 */
@Component
public class OrderMapper {

    private PersonMapper personMapper;
    private StoreMapper storeMapper;

    public OrderMapper(PersonMapper personMapper, StoreMapper storeMapper) {
        this.personMapper = personMapper;
        this.storeMapper = storeMapper;
    }

    public OrderView convertOrderToOrderView(Order order){
        if (order == null){return null;}

        List<String> orderItemDescriptions = new ArrayList<>();
        List<Integer> orderItemQuantities = new ArrayList<>();
        List<Double> orderItemPrices = new ArrayList<>();

        List<OrderItem> oi = order.getOrderItems(); //poor name - but its temp
        //EXEI GINEI HDH O ELEGXOS OTI TO ORDERFOODITEMS DEN EINAI EMPTY/NULL STO ORDER
        //for (OrderItem orderItem : oi){
          //  orderItemDescriptions.add(oi.get())
        //}
        for (int i=0;i<oi.size();i++){
            orderItemDescriptions.add(oi.get(i).getFoodItem().getDescription());
            orderItemQuantities.add(oi.get(i).getQuantity());
            orderItemPrices.add(oi.get(i).getPriceAtOrder());
        }

        return new OrderView(
            order.getId(),
            this.personMapper.convertPersonToPersonView(order.getPerson()),
            this.storeMapper.convertStoreToStoreView(order.getStore()),
            orderItemDescriptions,
            orderItemQuantities,
            orderItemPrices,
            order.getCreationDate(),
            order.getStatus(),
            order.getType()
        );
    }
}
