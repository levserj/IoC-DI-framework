package com.norsedigital.intedu.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.norsedigital.intedu.xml.InvalidXmlException;
import com.norsedigital.intedu.model.BeanDefinition;
import com.norsedigital.intedu.model.generated.Property;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by sl on 25.11.16.
 *
 * Returns list of beans read from the target xml file.
 */
public class SaxXMLParser extends AbstractParser {

    private static final Logger logger = Logger.getLogger(SaxXMLParser.class);

    @Override
    public Map<String, BeanDefinition> parseXmlToBeansDefinitionsMap(String xmlFilePath) throws InvalidXmlException {
        validateXml(xmlFilePath);
        ContextHandler contextHandler = new ContextHandler();
            try {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream(xsdSchemaPath)));
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setSchema(schema);
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(getISFromFile(xmlFilePath), contextHandler);
            } catch (Exception e) {
                logger.error(String.format("Error during parsing of xml (%1$s);\nException message : (%2$s).",
                        xmlFilePath, e.getMessage()));
            }
        return Collections.unmodifiableMap(contextHandler.beanDefinitionMap);
    }

    private class ContextHandler extends DefaultHandler {

        private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        private BeanDefinition beanDefinition;

        @Override
        public void startElement(String uri,
                                 String localName, String qName, Attributes attributes)
                throws SAXException {

            if (qName.equalsIgnoreCase("bean")) {
                String id = attributes.getValue("id");
                String clazz = attributes.getValue("class");
                String scope = attributes.getValue("scope");
                beanDefinition = BeanDefinition.create(id, clazz,scope);
            } else if (qName.equalsIgnoreCase("property")) {
                Property property = new Property();
                String name = attributes.getValue("name");
                String value = attributes.getValue("value");
                String ref = attributes.getValue("ref");
                if (isValidValue(name)) {
                    property.setName(name);
                }
                if (isValidValue(value)) {
                    property.setValue(value);
                }
                if (isValidValue(ref)) {
                    property.setRef(ref);
                }
                beanDefinition.getPropertyMap().put(name, property);
            }
        }

        @Override
        public void endElement(String uri,
                               String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("bean")) {
                logger.debug("Bean id = " + beanDefinition.getId());
                logger.debug("Bean class = " + beanDefinition.getClazz());
                logger.debug("Bean scope = " + beanDefinition.getScope());
                beanDefinitionMap.put(beanDefinition.getId(), beanDefinition);
            }
        }

        @Override
        public void characters(char ch[],
                               int start, int length) throws SAXException {
        }
    }

}

