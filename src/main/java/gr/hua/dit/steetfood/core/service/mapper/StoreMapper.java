package gr.hua.dit.steetfood.core.service.mapper;

import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.service.model.StoreView;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class StoreMapper {

    public StoreView convertStoreToStoreView (Store store){
        return new StoreView(
            store.getStoreId(),
            store.getStoreName(),
            store.getStoreAddress(),
            store.getPhoneNumber()
        );
    }
}
