package com.norsedigital.intedu.parser;

import com.norsedigital.intedu.xml.InvalidXmlException;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 24.11.16.
 */
public class JaxbXMLParserTest extends AbstractParserTest{

    @Before
    public void setUp() throws InvalidXmlException {
        parser = new JaxbXMLParser();
    }

    @Override
    public void checkReturnedBeanDefinitionMapHasCorrectData() throws InvalidXmlException {
        super.checkReturnedBeanDefinitionMapHasCorrectData();
    }
}
