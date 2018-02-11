package com.levserj.ioc.dependecy.injection.model;

import com.levserj.ioc.dependecy.injection.annotations.ContextBean;
import com.levserj.ioc.dependecy.injection.annotations.Inject;
import com.levserj.ioc.dependecy.injection.annotations.Value;

import java.io.Serializable;

/**
 * Created by sl on 28.11.16.
 */
@ContextBean("Dark_Side_user")
public class User implements Serializable{

    @Value("Palpatin")
    private String name;

    @Inject("workAddress")
    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
