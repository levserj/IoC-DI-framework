package com.norsedigital.intedu.parser;

import com.norsedigital.intedu.xml.InvalidXmlException;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 25.11.16.
 */
public class SaxXMLParserTest extends AbstractParserTest{

    @Before
    public void setUp() throws InvalidXmlException {
        parser = new SaxXMLParser();
    }

    @Override
    public void checkReturnedBeanDefinitionMapHasCorrectData() throws InvalidXmlException {
        super.checkReturnedBeanDefinitionMapHasCorrectData();
    }
}
