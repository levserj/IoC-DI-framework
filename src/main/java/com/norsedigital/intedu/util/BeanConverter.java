package com.norsedigital.intedu.util;

import com.norsedigital.intedu.model.BeanDefinition;
import com.norsedigital.intedu.model.generated.Bean;
import com.norsedigital.intedu.model.generated.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sl on 02.12.16.
 */
public class BeanConverter {

    public Map<String, BeanDefinition> convertBeansToBeansDefinitions(Map<String, Bean> input){
        Map<String, BeanDefinition> result = new HashMap<>();
        for (Bean bean : input.values()){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setId(bean.getId());
            beanDefinition.setClazz(bean.getClazz());
            for (Property p : bean.getProperty()){
                beanDefinition.getPropertyMap().put(p.getName(), p);
            }
            result.put(bean.getId(), beanDefinition);
        }
        return result;
    }
}
