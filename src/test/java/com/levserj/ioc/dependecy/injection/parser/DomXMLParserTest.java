package com.levserj.ioc.dependecy.injection.parser;

import com.levserj.ioc.dependecy.injection.xml.InvalidXmlException;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 25.11.16.
 */
public class DomXMLParserTest extends AbstractParserTest{

    @Before
    public void setUp() throws InvalidXmlException {
        parser = new DomXMLParser();
    }

    @Override
    public void checkReturnedBeanDefinitionMapHasCorrectData() throws InvalidXmlException {
        super.checkReturnedBeanDefinitionMapHasCorrectData();
    }
}
