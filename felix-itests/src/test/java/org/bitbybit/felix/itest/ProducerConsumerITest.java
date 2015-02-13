package org.bitbybit.felix.itest;

import org.bitbybit.consumer.api.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import javax.inject.Inject;

import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Itest using Felix framework
 */

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ProducerConsumerITest {

    @Inject
    private BundleContext bundleContext;

    @Inject
    private Consumer consumer;

    @Configuration
    public Option[] config() {

        return options(
                mavenBundle().groupId("org.bitbybit").artifactId("org.bitbybit.producer.api").versionAsInProject(),
                mavenBundle().groupId("org.bitbybit").artifactId("org.bitbybit.producer.impl").versionAsInProject(),
                mavenBundle().groupId("org.bitbybit").artifactId("org.bitbybit.consumer.api").versionAsInProject(),
                mavenBundle().groupId("org.bitbybit").artifactId("org.bitbybit.consumer.impl").versionAsInProject(),
                mavenBundle().groupId("org.apache.aries.blueprint").artifactId("org.apache.aries.blueprint"),
                mavenBundle().groupId("org.apache.aries.proxy").artifactId("org.apache.aries.proxy"),
                mavenBundle().groupId("org.apache.aries").artifactId("org.apache.aries.util"),
                junitBundles());
    }


    private Bundle getBundleBySymbolicName(final String symbolicName) {
        return Arrays.asList(bundleContext.
                getBundles()).
                stream().
                filter(bundle -> symbolicName.equals(bundle.getSymbolicName()))
                .findAny()
                .orElse(null);
    }

    @Test(expected=NullPointerException.class)
    public void testNullProducer() throws BundleException {
        getBundleBySymbolicName("org.bitbybit.producer.impl").stop();
        assertNull(consumer.consume());
    }

    @Test
    public void testNotNullProducer() throws BundleException {
        assertNotNull(consumer.consume());
    }
}
