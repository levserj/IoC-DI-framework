package com.norsedigital.intedu;

import com.norsedigital.intedu.DomXMLParser;
import com.norsedigital.intedu.model.Bean;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 25.11.16.
 */
public class DomXMLParserTest {

    private DomXMLParser converter;
    private List<Bean> beanList;
    private String xmlTestFilePath;

    @Before
    public void setUp() {
        xmlTestFilePath = "src/test/resources/test-parser-context.xml";
        converter = new DomXMLParser();
        beanList = converter.parseXmltoBeansList(xmlTestFilePath);
    }

    @Test
    public void checkReturnedBeanListHasCorrectBeanFromXmlWithAllTheFields() {

        assertThat(beanList.get(0).getId(), is("user"));
        assertThat(beanList.get(0).getClazz(), is("User"));
        assertThat(beanList.get(0).getProperty().get(0).getName(), is("fName"));
        assertThat(beanList.get(0).getProperty().get(0).getValue(), is("Petya"));
        assertThat(beanList.get(0).getProperty().get(1).getName(), is("lName"));
        assertThat(beanList.get(0).getProperty().get(1).getValue(), is("Petrov"));

    }
}
