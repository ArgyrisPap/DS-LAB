package gr.hua.dit.steetfood.core.service;

import gr.hua.dit.steetfood.core.model.FoodItem;
import gr.hua.dit.steetfood.core.model.Menu;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface MenuService {

    List<FoodItem> getMenuForStore(Long storeId);
}
