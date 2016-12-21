package com.norsedigital.intedu.xml;

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
import java.io.InputStream;


/**
 * Created by sl on 24.11.16.
 *
 * Validates xml file according to xsd file.
 */
public class XMLValidator {

    private static final Logger logger = Logger.getLogger(XMLValidator.class);
    private final String xsdSchemaPath;

    public XMLValidator(String xsdSchemaPath) {
        this.xsdSchemaPath = xsdSchemaPath;
    }

    public boolean validateXML (String xmlFilePath) {

        InputStream xml = XMLValidator.class.getResourceAsStream(xmlFilePath);
        InputStream xsd = XMLValidator.class.getResourceAsStream(xsdSchemaPath);
        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(xml);
            // create a SchemaFactory capable of understanding WXS schemas

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // load a WXS schema, represented by a Schema instance
            Source schemaFile = new StreamSource(xsd);
            Schema schema = factory.newSchema(schemaFile);

            // create a Validator instance, which can be used to validate an instance document
            Validator validator = schema.newValidator();

            // validate the DOM tree
            validator.validate(new DOMSource(document));
            return true;
        } catch (Exception e) {
            logger.error(String.format("XML validation error, invalid xml : %1$s;\n Exception : %2$s.",
                    xmlFilePath, e.getMessage()));
            return false;
        }
    }
}