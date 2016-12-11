package com.norsedigital.intedu;

import com.norsedigital.intedu.context.ContextHolder;
import com.norsedigital.intedu.context.ContextInitializer;
import com.norsedigital.intedu.model.User;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 08.12.16.
 */
public class ContextInitializerTest {

    private final Logger log = Logger.getLogger(this.getClass());

    private static ContextInitializer contextInitializer;
    private static ContextHolder holder;
    private static String pathToContext = "src/test/resources/test-context.xml";
    private static String pathToSchema = "src/test/resources/context.xsd";

    @BeforeClass
    public static void setUp(){
        contextInitializer = new ContextInitializer();
        contextInitializer.initializeContext(pathToContext, pathToSchema);
        holder = ContextHolder.INSTANCE;
    }

    @Test
    public void checkThatBeansFromXmlConfigInitialized(){
        User yoda = (User) holder.getBean("Light_Side_user");
        assertThat(yoda.getName(), is("Yoda"));
        assertThat(yoda.getAddress().getCity(), is("Odessa"));
        assertThat(yoda.getAddress().getPlace(), is("Norse Digital"));
    }

    @Test
    public void checkThatAnnotatedBeansInitialized(){
        User palpatin = (User) holder.getBean("Dark_Side_user");
        assertThat(palpatin.getName(), is("Palpatin"));
        assertThat(palpatin.getAddress().getCity(), is("Coruscant"));
        assertThat(palpatin.getAddress().getPlace(), is("Galactic Senate"));
    }

    @Test
    public void checkThatBeansWithPrototypeScopeArePointingToDifferentInstances(){
        User yoda = (User) holder.getBean("Light_Side_user");
        User clone = (User) holder.getBean("Light_Side_user");
        assertThat(yoda, is(not(sameInstance(clone))));
    }

    @Test
    public void checkThatBeansWithDefaultSingletonScopeArePointingToSameInstance(){
        User palpatin = (User) holder.getBean("Dark_Side_user");
        User fakeClone = (User) holder.getBean("Dark_Side_user");
        assertThat(palpatin, is(sameInstance(fakeClone)));
    }
}
