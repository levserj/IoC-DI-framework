package com.levserj.ioc.dependecy.injection.parser;

import com.levserj.ioc.dependecy.injection.model.BeanDefinition;
import com.levserj.ioc.dependecy.injection.xml.InvalidXmlException;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 21.12.16.
 */
public abstract class AbstractParserTest {

    protected Map<String, BeanDefinition> beanDefinitionMap;
    protected AbstractParser parser;
    protected String xmlTestFilePath = "/test-parser-context.xml";

    @Test
    public void checkReturnedBeanDefinitionMapHasCorrectData() throws InvalidXmlException {
        xmlTestFilePath = "/test-parser-context.xml";
        beanDefinitionMap = parser.parseXmlToBeansDefinitionsMap(xmlTestFilePath);

        assertThat(beanDefinitionMap.get("user").getClazz(), is("User"));
        assertThat(beanDefinitionMap.get("user").getPropertyMap().get("fName").getValue(), is("Petya"));
        assertThat(beanDefinitionMap.get("user").getPropertyMap().get("lName").getValue(), is("Petrov"));

    }
}
