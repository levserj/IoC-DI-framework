package com.norsedigital.intedu;

import com.norsedigital.intedu.util.XMLValidator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 24.11.16.
 */
public class ValidateXMLTest {

    private XMLValidator validator;
    private String location = "src/test/resources/";
    private String schema = location + "context.xsd";
    private String context = location + "test-parser-context.xml";
    private String wrongContext = location + "wrong-context.xml";

    @Before
    public void setUp(){
        validator = new XMLValidator();
    }

    @Test
    public void checkIfCorrectAndIncorrectContextsValidatedAsExpected(){
        assertThat(validator.validateXML(context, schema), is(true));
        assertThat(validator.validateXML(wrongContext, schema), is(false));
    }
}
