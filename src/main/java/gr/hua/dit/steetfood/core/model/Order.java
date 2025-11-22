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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(
    name="orders",
    indexes = {
        @Index(name = "idx_order_person_id", columnList = "person_id"),
        @Index(name = "idx_order_store_id", columnList = "store_id")
    }
)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "store_id")
    private Store store;
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "order_creation_date")
    private Instant creationDate;

    public Order() {}

    public Order(Long id, Person person, Store store, List<OrderItem> orderItems, Instant creationDate) {
        this.id = id;
        this.person = person;
        this.store = store;
        this.orderItems = orderItems;
        this.creationDate = creationDate;
    }
}
