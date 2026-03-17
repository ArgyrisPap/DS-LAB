package gr.hua.dit.steetfood.core.service.mapper;

import gr.hua.dit.steetfood.core.model.Store;
import gr.hua.dit.steetfood.core.service.model.StorePreview;
import gr.hua.dit.steetfood.core.service.model.StoreView;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Component
public class StoreMapper {

    public StoreView convertStoreToStoreView (Store store){
        return new StoreView(
            store.getStoreId(),
            store.getStoreName(),
            store.getStoreAddress(),
            store.getStoreType(),
            store.getPhoneNumber(),
            store.isOpen(),
            store.getMinOrder(),
            store.getFoodItemList()
        );
    }

    public List<StoreView> convertStoresToStoreView (List<Store> stores){


        ArrayList<StoreView> storeViewList= new ArrayList<>();
        if (stores== null || stores.isEmpty()) return storeViewList;
        for (Store store : stores){
            storeViewList.add(this.convertStoreToStoreView(store));
        }
        return storeViewList;
    }

    public StorePreview convertStoreToStorePreview (Store store){
        return new StorePreview(
            store.getStoreId(),
            store.getStoreName(),
            store.getStoreAddress(),
            store.getStoreType(),
            store.getPhoneNumber(),
            store.isOpen(),
            store.getMinOrder()
        );
    }
    public List<StorePreview> convertStoresToStorePreview (List<Store> stores){


        ArrayList<StorePreview> storePreviewList= new ArrayList<>();
        if (stores== null || stores.isEmpty()) return storePreviewList;
        for (Store store : stores){
            storePreviewList.add(this.convertStoreToStorePreview(store));
        }
        return storePreviewList;
    }
}
