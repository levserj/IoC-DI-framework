package com.norsedigital.intedu;

import com.norsedigital.intedu.context.ContextHolder;
import com.norsedigital.intedu.context.ContextInitializer;
import com.norsedigital.intedu.model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 08.12.16.
 */
public class ContextInitializerTest {

    private static ContextInitializer contextInitializer;
    private static Map<String, Object> context;
    private static String pathToContext = "src/test/resources/test-context.xml";
    private static String pathToSchema = "src/test/resources/context.xsd";

    @BeforeClass
    public static void setUp(){
        contextInitializer = new ContextInitializer();
        contextInitializer.initializeContext(pathToContext, pathToSchema);
        context = ContextHolder.INSTANCE.getContext();
    }

    @Test
    public void checkThatBeansFromXmlConfigInitialized(){
        User yoda = (User) context.get("Light_Side_user");
        assertThat(yoda.getName(), is("Yoda"));
        assertThat(yoda.getAddress().getCity(), is("Odessa"));
        assertThat(yoda.getAddress().getPlace(), is("Norse Digital"));
    }

    @Test
    public void checkThatAnnotationConfigWorks(){
        User palpatin = (User) context.get("Dark_Side_user");
        assertThat(palpatin.getName(), is("Palpatin"));
        assertThat(palpatin.getAddress().getCity(), is("Coruscant"));
        assertThat(palpatin.getAddress().getPlace(), is("Galactic Senate"));
    }
}
