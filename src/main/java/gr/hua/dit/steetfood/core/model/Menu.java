package gr.hua.dit.steetfood.core.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.List;

@Entity
@Table(name = "menu",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_menu_id", columnNames = "menu_id"),
        @UniqueConstraint( name= "uk_menu_store_id", columnNames = "menu_store_id")
    },
    indexes = {
        @Index(name= "idx_menu_store_id", columnList = "menu_store_id")
    }
)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "menu_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name= "menu_store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FoodItem> foodItems;

    public Menu(int id, Store store, List<FoodItem> foodItems) {
        this.id = id;
        this.store = store;
        this.foodItems = foodItems;
    }
    public Menu() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }
    @Override
    public String toString() {
        return "Menu{id=" + id + ", store=" + store + ", foodItems=" + foodItems + '}';
    }

}
