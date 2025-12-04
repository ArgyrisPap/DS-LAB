package gr.hua.dit.steetfood.core.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Address")
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String displayName;
    private Double latitude;
    private Double longitude;

    public Address(Long id,
                   Double longitude,
                   Double latitude,
                   String displayName
                   ) {
        this.id = id;
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Address() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
