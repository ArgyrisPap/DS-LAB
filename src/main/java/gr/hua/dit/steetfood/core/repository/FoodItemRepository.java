package gr.hua.dit.steetfood.core.repository;

import gr.hua.dit.steetfood.core.model.FoodItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem,Long> {

    Optional<FoodItem> findFoodItemByDescription(String foodItemDescription);


}
