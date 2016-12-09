package com.norsedigital.intedu.model;

import com.norsedigital.intedu.annotations.ContextBean;
import com.norsedigital.intedu.annotations.Value;

/**
 * Created by sl on 28.11.16.
 */
@ContextBean("workAddress")
public class Address {

    @Value("Coruscant")
    private String city;
    @Value("Galactic Senate")
    private String place;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
