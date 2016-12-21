package com.norsedigital.intedu.parser;

import com.norsedigital.intedu.xml.InvalidXmlException;
import com.norsedigital.intedu.model.BeanDefinition;
import com.norsedigital.intedu.xml.XMLValidator;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by sl on 20.12.16.
 */
public abstract class AbstractParser {

    private static final Logger logger = Logger.getLogger(AbstractParser.class);
    protected final String xsdSchemaPath = "/context.xsd";

    public abstract Map<String, BeanDefinition> parseXmlToBeansDefinitionsMap(String xmlFilePath) throws InvalidXmlException;

    protected void validateXml(String xmlPath) throws InvalidXmlException {

        XMLValidator validator = new XMLValidator(xsdSchemaPath);
        if (validator.validateXML(xmlPath)){
            logger.info(String.format("Xml file (%1$s) is valid", xmlPath));
        } else {
            logger.error(String.format("Invalid xml found (%1$s)", xmlPath));
            throw new InvalidXmlException(String.format("Invalid xml found (%1$s)", xmlPath));
        }
    }

    protected InputStream getISFromFile(String filePath){
        return AbstractParser.class.getResourceAsStream(filePath);
    }

    protected boolean isValidValue(String value){
        return (value != null && value.trim().length() > 0);
    }
}
