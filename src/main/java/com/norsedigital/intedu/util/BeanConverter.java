package com.norsedigital.intedu.util;

import com.norsedigital.intedu.model.CustomBean;
import com.norsedigital.intedu.model.generated.Bean;
import com.norsedigital.intedu.model.generated.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sl on 02.12.16.
 */
public class BeanConverter {

    public Map<String, CustomBean> convertBeanToCustomBean(Map<String, Bean> input){
        Map<String, CustomBean> result = new HashMap<>();
        for (Bean bean : input.values()){
            CustomBean customBean = new CustomBean();
            customBean.setId(bean.getId());
            customBean.setClazz(bean.getClazz());
            for (Property p : bean.getProperty()){
                customBean.getPropertyMap().put(p.getName(), p);
            }
            result.put(bean.getId(), customBean);
        }
        return result;
    }
}
