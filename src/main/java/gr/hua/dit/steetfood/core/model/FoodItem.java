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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Null;

import java.util.Objects;


@Entity
@Table(
    name = "food_item",
    uniqueConstraints = {
        @UniqueConstraint(name= "uk_food_item_id", columnNames = "food_item_id")
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
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;      //TODO NA GINEI STORE ANTI GIA MENU, KAI DELETE MENU GENIKA


    public FoodItem(Long id, String description, double price, final FoodCategory category, final Store store) {
        if (description == null ) throw new NullPointerException("description is null");
        if (description.isEmpty()) throw new IllegalArgumentException("description is empty");
        if (price <= 0) throw new IllegalArgumentException("price is negative");
        if (category == null) throw new NullPointerException("category is null");
        if (store == null) throw new NullPointerException("store is null");

        //TODO: ISWS XREIAZETAI NA TA SBHSW

        this.id=id;
        this.category = category;
        this.description = description;
        this.price = price;
        this.store = store;
    }
    public FoodItem() {}

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public FoodCategory getCategory() {return this.category;}

    public void setCategory(FoodCategory category) {this.category = category;}

    @Override
    public String toString() {
        return "Food Item{name="+description+",price="+price+",category="+category+"}";
    }
    //FOR .CONTAINS IN {@link OrderServiceImpl}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodItem)) return false;
        FoodItem other = (FoodItem) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
