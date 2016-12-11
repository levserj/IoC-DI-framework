package com.norsedigital.intedu.context;

import com.norsedigital.intedu.model.BeanDefinition;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sl on 28.11.16.
 */
public enum ContextHolder {
    INSTANCE;

    private final Logger log = Logger.getLogger(ContextHolder.class);
    private final Map<String, Object> Context = new ConcurrentHashMap<>();
    private Boolean annotationScan;
    private String packageToScan;

    public Object getBean(String beanId) {
        if (ContextInitializer.beansDefinitionsMap.get(beanId).getScope().equals("prototype")){
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

    Map<String, Object> getContext() {
        return Context;
    }

    Set<Map.Entry<String, Object>> getEntrySet(){
        return Context.entrySet();
    }

    public void setPackageToScan(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    public String getPackageToScan() {
        return packageToScan;
    }

    public Boolean getAnnotationScan() {
        return annotationScan;
    }

    public void setAnnotationScan(Boolean annotationScan) {
        this.annotationScan = annotationScan;
    }
}
