package com.levserj.ioc.dependecy.injection;

import com.levserj.ioc.dependecy.injection.xml.XMLValidator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 24.11.16.
 */
public class ValidateXMLTest {

    private XMLValidator validator;

    @Before
    public void setUp(){
        String xsdSchemaPath = "/context.xsd";
        validator = new XMLValidator(xsdSchemaPath);
    }

    @Test
    public void checkIfCorrectAndIncorrectContextsValidatedAsExpected(){

        String context = "/test-parser-context.xml";
        String wrongContext = "/wrong-context.xml";

        assertThat(validator.validateXML(context), is(true));
        assertThat(validator.validateXML(wrongContext), is(false));
    }
}
