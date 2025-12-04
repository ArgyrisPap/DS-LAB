package gr.hua.dit.steetfood.core.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

import javax.xml.stream.Location;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Person entity.
 */
@Entity
@Table(
    name = "person",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_person_hua_id", columnNames = "hua_id"),
        @UniqueConstraint(name = "uk_person_email_address", columnNames = "email_address"),
        @UniqueConstraint(name = "uk_person_mobile_phone_number", columnNames = "mobile_phone_number")
    },
    indexes = {
        @Index(name = "idx_person_type", columnList = "type"),
        @Index(name = "idx_person_last_name", columnList = "last_name")
    }
)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "hua_id", nullable = false, length = 20)
    private String huaId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "mobile_phone_number", nullable = false, length = 18)
    private String mobilePhoneNumber; // E164

    @Column(name = "email_address", nullable = false, length = 100)
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PersonType type;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Column (name="raw_address")
    private String rawAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;


    public Person() {
    }

    public Person(Long id,
                  String huaId,
                  String firstName,
                  String lastName,
                  String mobilePhoneNumber,
                  String emailAddress,
                  PersonType type,
                  String passwordHash,
                  Instant createdAt,
                  String rawAddress,
                  Address address) {
        this.id = id;
        this.huaId = huaId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.emailAddress = emailAddress;
        this.type = type;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.rawAddress = rawAddress;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHuaId() {
        return huaId;
    }

    public void setHuaId(String huaId) {
        this.huaId = huaId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }


    public String getRawAddress() {
        return rawAddress;
    }

    public void setRawAddress(String rawAddress) {
        this.rawAddress = rawAddress;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "Person{" +
            "id=" + id +
            ", huaId='" + huaId + '\'' +
            ", type=" + type + "locations="  +
            '}';
    }
}
