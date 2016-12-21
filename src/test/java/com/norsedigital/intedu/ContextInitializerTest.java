package com.norsedigital.intedu;

import com.norsedigital.intedu.context.ContextHolder;
import com.norsedigital.intedu.context.ContextInitializer;
import com.norsedigital.intedu.model.User;
import com.norsedigital.intedu.xml.InvalidXmlException;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by sl on 08.12.16.
 */
public class ContextInitializerTest {

    private static final Logger log = Logger.getLogger(ContextInitializerTest.class);

    private static ContextHolder holder;

    @BeforeClass
    public static void setUp() throws InvalidXmlException {
        String pathToContext = "/test-context.xml";
        new ContextInitializer().initializeContext(pathToContext);
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
