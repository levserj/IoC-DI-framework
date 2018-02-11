package com.levserj.ioc.dependecy.injection.model;

import com.levserj.ioc.dependecy.injection.annotations.ContextBean;
import com.levserj.ioc.dependecy.injection.annotations.Value;

import java.io.Serializable;

/**
 * Created by sl on 28.11.16.
 */
@ContextBean("workAddress")
public class Address implements Serializable {

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
