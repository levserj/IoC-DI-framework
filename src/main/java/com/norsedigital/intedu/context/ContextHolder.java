package com.norsedigital.intedu.context;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sl on 28.11.16.
 */
public enum ContextHolder {
    INSTANCE;

    private final Map<String, Object> Context = new ConcurrentHashMap<>();
    private Boolean annotationScan;
    private String packageToScan;

    public Object getBean(Object beanId) {
        return Context.get(beanId);
    }

    public void putBean(String beanId, Object bean){
        Context.put(beanId, bean);
    }

    public Set<Map.Entry<String, Object>> getEntrySet(){
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
