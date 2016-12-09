package com.norsedigital.intedu.parser;

import com.norsedigital.intedu.model.generated.Bean;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 24.11.16.
 */
public class JaxbXMLParserTest {

    private JaxbXMLParser converter;
    private Map<String, Bean> beanMap;
    private String xmlTestFilePath;
    private String pathToSchema;

    @Before
    public void setUp() {
        xmlTestFilePath = "src/test/resources/test-parser-context.xml";
        pathToSchema = "src/test/resources/context.xsd";
        converter = new JaxbXMLParser();
        beanMap = converter.parseXmltoBeansMap(xmlTestFilePath, pathToSchema);
    }

    @Test
    public void checkReturnedBeanListHasCorrectBeanFromXmlWithAllTheFields() {

        assertThat(beanMap.get("user").getClazz(), is("User"));
        assertThat(beanMap.get("user").getProperty().get(0).getName(), is("fName"));
        assertThat(beanMap.get("user").getProperty().get(0).getValue(), is("Petya"));
        assertThat(beanMap.get("user").getProperty().get(1).getName(), is("lName"));
        assertThat(beanMap.get("user").getProperty().get(1).getValue(), is("Petrov"));

    }
}
