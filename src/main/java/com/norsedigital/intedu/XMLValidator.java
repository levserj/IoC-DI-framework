package com.norsedigital.intedu;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;


/**
 * Created by sl on 24.11.16.
 *
 * Validates xml file according to xsd file.
 */
public class XMLValidator {

    private Logger logger = Logger.getLogger(this.getClass());

    public boolean validateXML (String xmlFilePath, String xsdFilePath) {

        // parse an XML document into a DOM tree
        DocumentBuilder parser = null;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(new File(xmlFilePath));
            // create a SchemaFactory capable of understanding WXS schemas

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // load a WXS schema, represented by a Schema instance
            Source schemaFile = new StreamSource(new File(xsdFilePath));
            Schema schema = factory.newSchema(schemaFile);

            // create a Validator instance, which can be used to validate an instance document
            Validator validator = schema.newValidator();

            // validate the DOM tree
            validator.validate(new DOMSource(document));
            return true;
        } catch (Exception e) {
            logger.error("INVALID XML FOUND : " + xmlFilePath + " : " + e);
            return false;
        }
    }
}
