package gr.hua.dit.steetfood.core.service.model;

import gr.hua.dit.steetfood.core.model.FoodCategory;

public record FoodItemView(
    Long id,
    String description,
    double price,
    FoodCategory category
) {
}
