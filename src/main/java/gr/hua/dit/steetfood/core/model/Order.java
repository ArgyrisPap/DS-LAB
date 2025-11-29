package gr.hua.dit.steetfood.core.model;


import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(
    name="order_table",
    indexes = {
        //@Index(name = "idx_order_person_id", columnList = "person_id"),
        //@Index(name = "idx_order_store_id", columnList = "store_id")
    }
)
public final class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_order_client"))
    private Person person;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "store_id",nullable = false, foreignKey = @ForeignKey(name = "fk_order_store"))
    private Store store;

    @NotNull
    @NotEmpty
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "order_creation_date")
    private Instant creationDate;

    @Enumerated (EnumType.STRING)
    @Column (name = "status", nullable = false, length = 16)
    private OrderStatus status;

    //@NotNull
    @Enumerated (EnumType.STRING)
    @Column (name = "type", nullable = false, length = 16)
    private OrderType type;

    @Column (name = "total_price")
    private Double totalPrice=0.0;


    public Order() {}

    public Order(Long id,
                 Person person,
                 Store store,
                 List<OrderItem> orderItems,
                 Instant creationDate,
                 OrderStatus status,
                 OrderType type) {
        this.id = id;
        this.person = person;
        this.store = store;
        this.orderItems = orderItems;
        updatePrice();
        this.creationDate = creationDate;
        this.status = status;
        this.type = type;
    }
    private void updatePrice (){ //INTERNAL METHOD FOR UPDATING PRICE EVERYTIME ORDERITEMS ARE UPDATED
        totalPrice=0.0;
        for (OrderItem orderItem : orderItems) {
            this.totalPrice +=  orderItem.getPriceAtOrder();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        updatePrice();
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }
}
