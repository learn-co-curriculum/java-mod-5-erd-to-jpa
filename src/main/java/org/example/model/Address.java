package org.example.model;

import javax.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue
    private int id;

    private String street;

    @Column(nullable=false)
    private String city;

    private String state, zip;

    @Column(columnDefinition = "varchar(100) default 'United States'")
    private String country = "United States";

    @OneToOne(mappedBy = "address")
    private Publisher publisher;

    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}

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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
