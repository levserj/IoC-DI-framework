package com.levserj.ioc.dependecy.injection.model;

import com.levserj.ioc.dependecy.injection.model.generated.Property;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sl on 02.12.16.
 */

public class BeanDefinition {

    private static final Logger log = Logger.getLogger(BeanDefinition.class);

    private String id;
    private String clazz;
    private String scope;
    private Map<String, Property> propertyMap;

    public static BeanDefinition create(String id, String clazz, String scope) {
        if (isValidValue(id) && isValidValue(clazz) && isValidValue(scope)) {
            return new BeanDefinition(id, clazz, scope);
        }
        log.error(String.
                format("Wrong arguments passed to the BeanDefinition constructor :id = (%1$s), clazz = (%2$s), scope = (%3$s)",
                        id, clazz, scope));
        return null;
    }

    private BeanDefinition(String id, String clazz, String scope) {
        this.id = id;
        this.clazz = clazz;
        this.scope = scope;
    }

    public String getId() {
        return id;
    }

    public String getClazz() {
        return clazz;
    }

    public Map<String, Property> getPropertyMap() {
        if (propertyMap == null) {
            propertyMap = new HashMap<>();
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

    private static boolean isValidValue(String value) {
        return (value != null && value.trim().length() > 0);
    }
}
