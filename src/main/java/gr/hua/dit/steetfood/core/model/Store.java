package gr.hua.dit.steetfood.core.model;


import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.List;

@Entity
@Table(
    name="store",
    uniqueConstraints = {
        @UniqueConstraint(name="uk_store_id", columnNames = "store_id"),
        @UniqueConstraint(name ="uk_store_address", columnNames = "store_address"),
        @UniqueConstraint(name="uk_store_phone_number", columnNames = "store_phone_number")
        /*Assumptions:
        1. stores can share the same name
        2. stores cannnot share address
        */
    },
    indexes = {
        @Index(name = "idx_store_type", columnList = "store_type"),
        @Index(name = "idx_store_address", columnList = "store_address")
    }
)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name= "store_id")
    private Long id;

    @Column(name="store_name", nullable = false, length = 50)
    private String storeName;

    @Column(name = "store_address", nullable = false, length = 30)
    private String storeAddress;

    @Enumerated(EnumType.STRING)
    @Column(name= "store_type")
    private StoreType storeType;

    @Column(name ="store_phone_number", nullable = false, length = 18)
    private String phoneNumber; //E164

    @Column(name = "store_open")
    private boolean open = true; //TODO NA TO KANW KATEYUEIAN FALSE

    @NotNull
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<FoodItem> foodItemList;

    public Store(){}

    public Store(Long id, String storeName, String storeAddress, StoreType storeType, String phoneNumber,boolean open, List<FoodItem> foodItemList) {
        this.id = id;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeType = storeType;
        this.phoneNumber = phoneNumber;
        this.open = open;
        this.foodItemList = foodItemList;
    }
    public Long getId() { //FOR THYMELEAF!!!
        return id;
    }

    public List<FoodItem> getFoodItemList() {
        return foodItemList;
    }

    public void setFoodItemList(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
    }

    public Long getStoreId() {
        return id;
    }

    public void setStoreId(Long id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String store_address) {
        this.storeAddress = store_address;
    }

    public StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "Store{id =" +id +", storeName='"+storeName+'\''+
            ", phoneNumber='"+phoneNumber+'\''+"+is:" +open+'}';
    }
}
