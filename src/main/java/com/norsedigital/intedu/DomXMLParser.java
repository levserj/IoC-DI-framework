package com.norsedigital.intedu;

import com.norsedigital.intedu.model.Bean;
import com.norsedigital.intedu.model.Property;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sl on 24.11.16.
 *
 * Returns list of beans read from the target xml file.
 */
public class DomXMLParser {

    private Logger logger = Logger.getLogger(this.getClass());
    private List<Bean> beans = new ArrayList<Bean>();

    public List<Bean> parseXmltoBeansList(String xmlFilePath) {

        if (new XMLValidator().validateXML(xmlFilePath, "src/test/resources/context.xsd")) {
            try {
                File inputFile = new File(xmlFilePath);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("bean");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Bean bean = new Bean();
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String id = eElement.getAttribute("id");
                        String clazz = eElement.getAttribute("class");
                        logger.debug("Bean id : " + id);
                        logger.debug("Bean class : " + clazz);
                        bean.setId(id);
                        bean.setClazz(clazz);
                        NodeList propList = eElement.getElementsByTagName("property");
                        for (int i = 0; i < propList.getLength(); i++) {
                            Property property = new Property();
                            NamedNodeMap namedNodeMap = propList.item(i).getAttributes();
                            if (namedNodeMap.getNamedItem("name") != null) {
                                property.setName(namedNodeMap.getNamedItem("name").getNodeValue());
                            }
                            if (namedNodeMap.getNamedItem("value") != null) {
                                property.setValue(namedNodeMap.getNamedItem("value").getNodeValue());
                            }
                            if (namedNodeMap.getNamedItem("ref") != null) {
                                property.setRef(namedNodeMap.getNamedItem("ref").getNodeValue());
                            }
                            bean.getProperty().add(property);
                        }
                    }
                    beans.add(bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.error("Invalid xml");
        }
        return beans;
    }
}
