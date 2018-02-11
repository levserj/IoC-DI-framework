package com.levserj.ioc.dependecy.injection.parser;

import com.levserj.ioc.dependecy.injection.context.ContextHolder;
import com.levserj.ioc.dependecy.injection.model.BeanDefinition;
import com.levserj.ioc.dependecy.injection.model.generated.Bean;
import com.levserj.ioc.dependecy.injection.model.generated.Context;
import com.levserj.ioc.dependecy.injection.xml.InvalidXmlException;
import com.levserj.ioc.dependecy.injection.model.generated.ObjectFactory;
import com.levserj.ioc.dependecy.injection.model.generated.Property;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.*;

/**
 * Created by sl on 24.11.16.
 * <p>
 * Returns list of beans read from the target xml file.
 */
public class JaxbXMLParser extends AbstractParser {

    private static final Logger logger = Logger.getLogger(JaxbXMLParser.class);

    @Override
    public Map<String, BeanDefinition> parseXmlToBeansDefinitionsMap(String xmlFilePath) throws InvalidXmlException {
        validateXml(xmlFilePath);
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        try {
            InputStream xmlIS = getISFromFile(xmlFilePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<Context> element = (JAXBElement<Context>) jaxbUnmarshaller.unmarshal(xmlIS);
            ContextHolder.INSTANCE.setAnnotationScan(element.getValue().getAnnotationScan().isEnabled());
            ContextHolder.INSTANCE.setPackageToScan(element.getValue().getAnnotationScan().getPackage());
            List<Bean> beanList = element.getValue().getBean();
            for (Bean bean : beanList){
                String id = bean.getId();
                String clazz = bean.getClazz();
                String scope = bean.getScope();
                BeanDefinition beanDefinition = BeanDefinition.create(id, clazz, scope);
                Map<String, Property> propertyMap = new HashMap<>();
                for (Property p : bean.getProperty()){
                    propertyMap.put(p.getName(), p);
                }
                beanDefinition.setPropertyMap(propertyMap);
                beanDefinitionMap.put(bean.getId(), beanDefinition);
            }
        } catch (JAXBException e) {
            logger.error(String.format("Error during parsing of xml (%1$s);\nException message : (%2$s).",
                    xmlFilePath, e.getMessage()));
        }
        beanDefinitionMap.entrySet().forEach(bean -> {
            logger.debug("Bean id = " + bean.getValue().getId());
            logger.debug("Bean class = " + bean.getValue().getClazz());
            logger.debug("Bean scope = " + bean.getValue().getScope());
        });
        return Collections.unmodifiableMap(beanDefinitionMap);
    }


}
