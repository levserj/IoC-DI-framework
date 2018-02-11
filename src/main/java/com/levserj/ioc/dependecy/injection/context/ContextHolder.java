package com.levserj.ioc.dependecy.injection.context;

import com.levserj.ioc.dependecy.injection.model.BeanDefinition;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sl on 28.11.16.
 */
public enum ContextHolder {
    INSTANCE;

    private static final Logger log = Logger.getLogger(ContextHolder.class);
    private final Map<String, Object> Context = new ConcurrentHashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private Boolean annotationScan;
    private String packageToScan;

    public Object getBean(String beanId) {
        if (beanDefinitionMap.get(beanId).getScope().equals("prototype")){
            return deepCopy(Context.get(beanId));
        }
        return Context.get(beanId);
    }

    private Object deepCopy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream(5*1024);
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        }
        catch(IOException | ClassNotFoundException e) {
            log.error("Failed to create copy of the object : " + orig);
        }
        return obj;
    }

    void putBean(String beanId, Object bean){
        Context.put(beanId, bean);
    }

    void addBeanDefinitions(Map<String, BeanDefinition> map){
        beanDefinitionMap.putAll(map);
    }

    Map<String, Object> getContext() {
        return Context;
    }

    public void setPackageToScan(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    String getPackageToScan() {
        return packageToScan;
    }

    Boolean getAnnotationScan() {
        return annotationScan;
    }

    public void setAnnotationScan(Boolean annotationScan) {
        this.annotationScan = annotationScan;
    }
}
