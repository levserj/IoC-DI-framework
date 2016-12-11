package com.norsedigital.intedu.context;

import com.norsedigital.intedu.util.BeanConverter;
import com.norsedigital.intedu.annotations.*;
import com.norsedigital.intedu.model.BeanDefinition;
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
    private Map<String, BeanDefinition> beansDefinitionsMap = new HashMap<>();
    private Map<BeanDefinition, List<Property>> beansDefinitionsWithDependencies = new HashMap<>();
    private ContextHolder holder = ContextHolder.INSTANCE;

    public void initializeContext(String pathToContext, String pathToSchema) {
        Map<String, Bean> beansMap = parser.parseXmltoBeansMap(pathToContext, pathToSchema);
        log.debug("ANNOTATION SCAN IS : " + holder.getAnnotationScan());
        if (holder.getAnnotationScan()) {
            createBeansDefinitionsUsingAnnotations(holder.getPackageToScan());
        }
        Map<String, BeanDefinition> beansDefinitionsFromXMLConfig = new BeanConverter().convertBeansToBeansDefinitions(beansMap);
        beansDefinitionsMap.putAll(beansDefinitionsFromXMLConfig);
        createAllBeansFromBeansDefinition();
        injectDependenciesToBeans();
    }

    private void createBeansDefinitionsUsingAnnotations(String packageToScan) {
        FastClasspathScanner scanner = new FastClasspathScanner(packageToScan);
        scanner.enableMethodAnnotationIndexing();
        ScanResult scanResult = scanner.scan();
        List<String> classesList = scanResult.getNamesOfClassesWithAnnotation(ContextBean.class);
        String beanId;
        for (String className : classesList) {
            Class<?> clazz = getClassByClassName(className);
            ContextBean annotation = clazz.getDeclaredAnnotation(ContextBean.class);
            beanId = annotation.value();
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setId(beanId);
            beanDefinition.setClazz(className);
            addPropertiesToBeansDefinitions(beanDefinition);
            beansDefinitionsMap.put(beanId, beanDefinition);
        }
    }

    private void addPropertiesToBeansDefinitions(BeanDefinition beanDefinition) {
        Class<?> clazz = getClassByClassName(beanDefinition.getClazz());
        List<Field> annotatedFields = getAnnotatedFields(clazz);
        for (Field field : annotatedFields) {
            field.setAccessible(true);
            String propertyName = field.getName();
            Property property = new Property();
            property.setName(propertyName);
            if (field.getAnnotation(Value.class) != null) {
                Value valueAnnotation = field.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                property.setValue(value);
            }
            if (field.getAnnotation(Inject.class) != null) {
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                String dependencyId = injectAnnotation.value();
                property.setRef(dependencyId);
            }
            beanDefinition.getPropertyMap().put(propertyName, property);
        }
    }

    private void createAllBeansFromBeansDefinition(){
        for (BeanDefinition bean : beansDefinitionsMap.values()) {
            Object object = createBeanFromBeanDefinition(bean);
            holder.putBean(bean.getId(), object);
        }
    }

    private Object createBeanFromBeanDefinition(BeanDefinition bean) {
        Class<?> clazz = getClassByClassName(bean.getClazz());
        Object object = createObjectFromClass(clazz);
        List<Property> propertiesWithRef = new ArrayList<>();
        for (Property p : bean.getPropertyMap().values()) {
            String pName = p.getName();
            String pRef = p.getRef();
            String pValue = p.getValue();
            Field field = getClassField(clazz, pName);
            setObjectsFieldValue(object, field, pValue);
            if (pRef != null) {
                propertiesWithRef.add(p);
            }
        }
        if (!propertiesWithRef.isEmpty()){
            beansDefinitionsWithDependencies.put(bean, propertiesWithRef);
        }
        return object;
    }

    private void injectDependenciesToBeans() {
        for (Map.Entry<BeanDefinition, List<Property>> entry : beansDefinitionsWithDependencies.entrySet()){
            String beanWithDepId = entry.getKey().getId();
            Class<?> clazz = getClassByClassName(entry.getKey().getClazz());
            for (Property p : entry.getValue()){
                String pName = p.getName();
                String dependencyId = p.getRef();
                Field refField = getClassField(clazz, pName);
                injectDependencyToBean(beanWithDepId, dependencyId, refField);
            }
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
        } catch (Exception e) {
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
            Annotation[] annotations = f.getDeclaredAnnotations();
            if (annotations.length > 0) {
                for (Annotation a : annotations) {
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

