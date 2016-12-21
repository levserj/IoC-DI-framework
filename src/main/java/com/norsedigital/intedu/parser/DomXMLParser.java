package com.norsedigital.intedu.parser;

import com.norsedigital.intedu.xml.InvalidXmlException;
import com.norsedigital.intedu.model.BeanDefinition;
import com.norsedigital.intedu.model.generated.Property;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.util.*;

/**
 * Created by sl on 24.11.16.
 * <p>
 * Returns list of beans read from the target xml file.
 */
public class DomXMLParser extends AbstractParser {

    private static final Logger logger = Logger.getLogger(DomXMLParser.class);

    @Override
    public Map<String, BeanDefinition> parseXmlToBeansDefinitionsMap(String xmlFilePath) throws InvalidXmlException {
        validateXml(xmlFilePath);
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new StreamSource(getClass().getResourceAsStream(xsdSchemaPath)));
            dbFactory.setSchema(schema);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(getISFromFile(xmlFilePath));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("bean");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Map<String, Property> properties = new HashMap<>();
                    NodeList propList = eElement.getElementsByTagName("property");
                    for (int i = 0; i < propList.getLength(); i++) {
                        Property property = new Property();
                        NamedNodeMap namedNodeMap = propList.item(i).getAttributes();
                        Node name = namedNodeMap.getNamedItem("name");
                        Node value = namedNodeMap.getNamedItem("value");
                        Node ref = namedNodeMap.getNamedItem("ref");
                        if (name != null && isValidValue(name.getNodeValue())) {
                            property.setName(name.getNodeValue());
                        }
                        if (value != null && isValidValue(value.getNodeValue())) {
                            property.setValue(value.getNodeValue());
                        }
                        if (ref != null && isValidValue(ref.getNodeValue())) {
                            property.setRef(ref.getNodeValue());
                        }
                        properties.put(property.getName(), property);
                    }
                    String id = eElement.getAttribute("id");
                    String clazz = eElement.getAttribute("class");
                    String scope = eElement.getAttribute("scope");
                    logger.debug("Bean id : " + id);
                    logger.debug("Bean class : " + clazz);
                    logger.debug("Bean scope : " + scope);
                    BeanDefinition beanDefinition = BeanDefinition.create(id, clazz, scope);
                    beanDefinition.setPropertyMap(properties);
                    beanDefinitions.put(beanDefinition.getId(), beanDefinition);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Error during parsing of xml (%1$s);\nException message : (%2$s).",
                    xmlFilePath, e.getMessage()));
        }
        return Collections.unmodifiableMap(beanDefinitions);
    }
}
