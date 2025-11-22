package gr.hua.dit.steetfood.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Null;


@Entity
@Table(
    name = "food_item",
    uniqueConstraints = {
        @UniqueConstraint(name= "uk_food_item_id", columnNames = "food_item_id"),
        @UniqueConstraint(name = "uk_food_item_description",columnNames = "food_item_description")
    },
    indexes = {
        @Index(name ="idx_food_item_description", columnList = "food_item_description")
    }
)

public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name ="food_item_id")
    private Long id;

    @Column(name="food_item_description",nullable = false,length = 20)
    private  String description;

    @Column(name = "food_item_price")
    private  double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_item_category")
    private FoodCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;


    public FoodItem(Long id, String description, double price, final FoodCategory category) {
        if (description == null ) throw new NullPointerException("description is null");
        if (description.isEmpty()) throw new IllegalArgumentException("description is empty");
        if (price <= 0) throw new IllegalArgumentException("price is negative");
        if (category == null) throw new NullPointerException("category is null");

        //TODO: ISWS XREIAZETAI NA TA SBHSW

        this.id=id;
        this.category = category;
        this.description = description;
        this.price = price;
    }
    public FoodItem() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public FoodCategory getCategory() {return this.category;}

    public void setCategory(FoodCategory category) {this.category = category;}

    @Override
    public String toString() {
        return "Food Item{name="+description+",price="+price+",category="+category+"}";
    }
}
