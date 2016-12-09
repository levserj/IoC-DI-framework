package com.norsedigital.intedu.parser;

import com.norsedigital.intedu.context.ContextHolder;
import com.norsedigital.intedu.model.generated.Bean;
import com.norsedigital.intedu.model.generated.Context;
import com.norsedigital.intedu.model.generated.ObjectFactory;
import com.norsedigital.intedu.util.XMLValidator;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sl on 24.11.16.
 *
 * Returns list of beans read from the target xml file.
 */
public class JaxbXMLParser {

    private Logger logger = Logger.getLogger(this.getClass());
    private List<Bean> beanList = new ArrayList<>();
    private Map<String, Bean> beanMap = new HashMap<>();

    public Map<String, Bean> parseXmltoBeansMap(String pathToXmlFile, String pathToSchema){
        if (new XMLValidator().validateXML(pathToXmlFile, pathToSchema)) {
            try {
                File file = new File(pathToXmlFile);
                JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Context> element = (JAXBElement<Context>) jaxbUnmarshaller.unmarshal(file);
                ContextHolder.INSTANCE.setAnnotationScan(element.getValue().getAnnotationScan().isEnabled());
                ContextHolder.INSTANCE.setPackageToScan(element.getValue().getAnnotationScan().getPackage());
                beanList = element.getValue().getBean();
                beanList.forEach(bean -> {
                    beanMap.put(bean.getId(), bean);
                });
            } catch (JAXBException e) {
                logger.debug("XML parsing error : " + e.getMessage());
            }
            beanList.forEach(bean -> {
                logger.debug("ContextBean id = " + bean.getId());
                logger.debug("ContextBean class = " + bean.getClazz());
            });
        } else{
            logger.error("Invalid XML");
        }
        return beanMap;
    }
}
