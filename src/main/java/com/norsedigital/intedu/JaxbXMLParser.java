package com.norsedigital.intedu;

import com.norsedigital.intedu.model.Bean;
import com.norsedigital.intedu.model.Context;

import com.norsedigital.intedu.model.ObjectFactory;
import org.apache.log4j.Logger;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sl on 24.11.16.
 *
 * Returns list of beans read from the target xml file.
 */
public class JaxbXMLParser {

    private Logger logger = Logger.getLogger(this.getClass());
    private List<Bean> beanList = new ArrayList<>();

    public List<Bean> parseXmltoBeansList(String xmlFilePath){
        if (new XMLValidator().validateXML(xmlFilePath, "src/test/resources/context.xsd")) {
            try {
                File file = new File(xmlFilePath);
                JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Context> element = (JAXBElement<Context>) jaxbUnmarshaller.unmarshal(file);
                beanList = element.getValue().getBean();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
            beanList.forEach(bean -> {
                logger.debug("Bean id = " + bean.getId());
                logger.debug("Bean class = " + bean.getClazz());
            });
        } else{
            logger.error("Invalid xml");
        }
        return beanList;
    }
}
