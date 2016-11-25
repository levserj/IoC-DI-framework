package com.norsedigital.intedu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.norsedigital.intedu.model.Bean;
import com.norsedigital.intedu.model.Property;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by sl on 25.11.16.
 *
 * Returns list of beans read from the target xml file.
 */
public class SaxXMLParser {

    private Logger logger = Logger.getLogger(this.getClass());

    public List<Bean> parseXmlToBeansList(String xmlFilePath) {
        ContextHandler contextHandler = new ContextHandler();
        if (new XMLValidator().validateXML(xmlFilePath, "src/test/resources/context.xsd")) {
            try {
                File inputFile = new File(xmlFilePath);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(inputFile, contextHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.error("Invalid xml");
        }
        return contextHandler.getBeanList();
    }
}

class ContextHandler extends DefaultHandler {

    private Logger logger = Logger.getLogger(this.getClass());
    private List<Bean> beanList = new ArrayList<>();
    private Bean bean;

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("bean")) {
            bean = new Bean();
            String id = attributes.getValue("id");
            String clazz = attributes.getValue("class");
            bean.setId(id);
            bean.setClazz(clazz);
        } else if (qName.equalsIgnoreCase("property")) {
            Property property = new Property();
            if (attributes.getValue("name") != null) {
                String name = attributes.getValue("name");
                property.setName(name);
            }
            if (attributes.getValue("value") != null) {
                String value = attributes.getValue("value");
                property.setValue(value);
            }
            if (attributes.getValue("ref") != null) {
                String ref = attributes.getValue("ref");
                property.setRef(ref);
            }
            bean.getProperty().add(property);
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("bean")) {
            logger.debug("Bean id = " + bean.getId());
            logger.debug("Bean class = " + bean.getClazz());
            beanList.add(bean);
        }
    }

    @Override
    public void characters(char ch[],
                           int start, int length) throws SAXException {
    }

    List<Bean> getBeanList() {
        return beanList;
    }
}

