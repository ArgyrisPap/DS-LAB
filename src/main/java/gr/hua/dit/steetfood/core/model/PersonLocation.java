package gr.hua.dit.steetfood.core.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(
    name= "person_location"
)
//TODO RESTRICTION NA MHN MPOREI TO IDIO PERSON ID NA BALEI TO IDIO ADDRESS DYO FORES
public class PersonLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Long id;

    @Column (name = "zip_code")
    private int zipCode; //TK

    @Column (name = "street")
    private String street;

    @Column (name = "city")
    private String city; //Αιγαλεω, κορυδαλλος...

    @Column (name = "state")
    private String state ; //ATTIKH only

    @Column (name = "street_number")
    private String streetNumber; //String because of  street  number: "2A"

    @ManyToOne
    private Person person;

    public PersonLocation(Long id,
                          int zipCode,
                          String street,
                          String city,
                          String state,
                          String streetNumber,
                          Person person) {
        this.id = id;
        this.zipCode = zipCode;
        this.street = street;
        this.city = city;
        this.state = state;
        this.streetNumber = streetNumber;
        this.person = person;
    }
    public PersonLocation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson (Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return street + city + state + streetNumber;
    }
}
