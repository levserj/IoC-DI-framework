package com.norsedigital.intedu.model;

import com.norsedigital.intedu.model.generated.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sl on 02.12.16.
 */

public class BeanDefinition {

    private String id;
    private String clazz;
    private String scope;
    private Map<String, Property> propertyMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Map<String, Property> getPropertyMap() {
        if (propertyMap == null){
            propertyMap = new HashMap<String, Property>();
        }
        return propertyMap;
    }

    public void setPropertyMap(Map<String, Property> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
