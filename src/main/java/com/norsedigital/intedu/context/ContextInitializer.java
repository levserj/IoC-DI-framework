package com.norsedigital.intedu.context;

import com.norsedigital.intedu.util.BeanConverter;
import com.norsedigital.intedu.annotations.*;
import com.norsedigital.intedu.model.CustomBean;
import com.norsedigital.intedu.model.generated.Bean;
import com.norsedigital.intedu.model.generated.Property;
import com.norsedigital.intedu.parser.JaxbXMLParser;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sl on 28.11.16.
 */
public class ContextInitializer {

    private final Logger log = Logger.getLogger(this.getClass());

    private JaxbXMLParser parser = new JaxbXMLParser();
    private Map<String, Bean> beansMap;
    private Map<String, CustomBean> customBeansMap;
    private Map<String, CustomBean> beansWithDependencies = new HashMap<>();
    private ContextHolder holder = ContextHolder.INSTANCE;

    public void initializeContext(String pathToContext, String pathToSchema) {
        beansMap = parser.parseXmltoBeansMap(pathToContext, pathToSchema);
        log.debug("ANNOTATION SCAN IS : " + holder.getAnnotationScan());
        if (holder.getAnnotationScan()) {
            createAllBeansUsingAnnotations(holder.getPackageToScan());
            injectDependenciesToBeansUsingAnnotations();
        }
        createAllBeansUsingXml();
        injectDependenciesToBeansUsingXml();
    }

    private void createAllBeansUsingXml(){
        customBeansMap = new BeanConverter().convertBeanToCustomBean(beansMap);
        for (CustomBean bean : customBeansMap.values()) {
            Object object = createBeanUsingXmlConfig(customBeansMap.get(bean.getId()));
            holder.putBean(bean.getId(), object);
        }
    }
    private void createAllBeansUsingAnnotations(String packageToScan) {
        FastClasspathScanner scanner = new FastClasspathScanner(packageToScan);
        scanner.enableMethodAnnotationIndexing();
        ScanResult scanResult = scanner.scan();
        List<String> classesList = scanResult.getNamesOfClassesWithAnnotation(ContextBean.class);
        String beanId;
        for (String className : classesList) {
            Class<?> clazz = getClassByClassName(className);
            ContextBean annotation = clazz.getDeclaredAnnotation(ContextBean.class);
            beanId = annotation.value();
            Object object = createBeanUsingAnnotations(className);
            holder.putBean(beanId, object);
        }
    }

    private Object createBeanUsingAnnotations(String className) {
        Class<?> clazz = getClassByClassName(className);
        Object object = createObjectFromClass(clazz);
        List<Field> annotatedFields = getAnnotatedFields(clazz);
        for (Field field : annotatedFields) {
            field.setAccessible(true);
            if (field.getAnnotation(Value.class) != null) {
                Value valueAnnotation = field.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                setObjectsFieldValue(object, field, value);
            }
        }
        return object;
    }

    private void injectDependenciesToBeansUsingAnnotations() {
        for (Map.Entry<String, Object> entry : holder.getEntrySet()) {
            String beanId = entry.getKey();
            List<Field> annotatedFields = getAnnotatedFields(entry.getValue().getClass());
            for (Field field : annotatedFields) {
                field.setAccessible(true);
                if (field.getAnnotation(Inject.class) != null) {
                    Inject injectAnnotation = field.getAnnotation(Inject.class);
                    String dependencyId = injectAnnotation.value();
                    injectDependencyToBean(beanId, dependencyId, field);
                }
            }
        }
    }

    private Object createBeanUsingXmlConfig(CustomBean bean) {
        Class<?> clazz = getClassByClassName(bean.getClazz());
        Object object = createObjectFromClass(clazz);
        for (Property p : bean.getPropertyMap().values()) {
            String pName = p.getName();
            String pRef = p.getRef();
            String pValue = p.getValue();
            Field field = getClassField(clazz, pName);
            setObjectsFieldValue(object, field, pValue);
            if (pRef != null) {
                beansWithDependencies.put(pName, bean);
            }
        }
        return object;
    }

    private void injectDependenciesToBeansUsingXml() {
        for (Map.Entry<String, CustomBean> entry : beansWithDependencies.entrySet()){
            String pName = entry.getKey();
            String dependencyId = entry.getValue().getPropertyMap().get(pName).getRef();
            String beanWithDepId = entry.getValue().getId();
            String className = entry.getValue().getClazz();
            Class<?> clazz = getClassByClassName(className);
            Field refField = getClassField(clazz, pName);
            injectDependencyToBean(beanWithDepId, dependencyId, refField);
        }
    }

    private Object setObjectsFieldValue(Object object, Field field, String fieldValue) {
        field.setAccessible(true);
        String type = field.getType().getTypeName();
        try {
            if (field.getType().isPrimitive()) {
                if (type.equals("byte")) {
                    field.setByte(object, Byte.parseByte(fieldValue));
                }
                if (type.equals("short")) {
                    field.setShort(object, Short.parseShort(fieldValue));
                }
                if (type.equals("int")) {
                    field.setInt(object, Integer.parseInt(fieldValue));
                }
                if (type.equals("long")) {
                    field.setLong(object, Long.parseLong(fieldValue));
                }
                if (type.equals("float")) {
                    field.setFloat(object, Float.parseFloat(fieldValue));
                }
                if (type.equals("double")) {
                    field.setDouble(object, Double.parseDouble(fieldValue));
                }
                if (type.equals("boolean")) {
                    field.setBoolean(object, Boolean.parseBoolean(fieldValue));
                }
                if (type.equals("char")) {
                    field.setChar(object, fieldValue.charAt(0));
                }
            } else {
                field.set(object, fieldValue);
            }
        } catch (IllegalAccessException e) {
            log.error(String.format("Failed to set objects (%1$s) field (%2$s) value (%3$s);\n Exception message : (%4$s).",
                    object.getClass().getName(), field.getName(), fieldValue, e.getMessage()));
        }
        return object;
    }

    private void injectDependencyToBean(String beanId, String dependencyId, Field fieldToInjectDependency) {
        Object bean = holder.getBean(beanId);
        Object dependency = holder.getBean(dependencyId);
        try {
            fieldToInjectDependency.set(bean, dependency);
        } catch (IllegalAccessException e) {
            log.error(String.format("Failed to inject dependency (%1$s) to beans (%2$s) field (%3$s);\n Exception message : (%4$s).",
                    dependencyId, beanId, fieldToInjectDependency.getName(), e.getMessage()));
        }
    }

    private List<Field> getAnnotatedFields(Class<?> clazz) {
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> result = new ArrayList<>();
        for (Field f : allFields) {
            f.setAccessible(true);
            Annotation[] annotaions = f.getDeclaredAnnotations();
            if (annotaions.length > 0) {
                for (Annotation a : annotaions) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    private Class<?> getClassByClassName(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error(String.format("Failed to get class by class name (%1$s);\n Exception message : (%2$s).",
                    className, e.getMessage()));
        }
        return clazz;
    }

    private Object createObjectFromClass(Class<?> clazz) {
        Object object = null;
        try {
            object = clazz.newInstance();
        } catch (Exception e) {
            log.error(String.format("Failed to create object by class name (%1$s);\n Exception message : (%2$s).",
                    clazz, e.getMessage()));
        }
        return object;
    }


    private Field getClassField(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            log.error(String.format("Failed to get field (%1$s) from class (%2$s);\n Exception message : (%3$s).",
                    fieldName, clazz, e.getMessage()));
        }
        return field;
    }
}

