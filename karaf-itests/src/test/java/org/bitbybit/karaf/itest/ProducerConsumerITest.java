package org.bitbybit.karaf.itest;

import org.bitbybit.consumer.api.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import javax.inject.Inject;

import java.io.File;
import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

/**
 * Itest using Karaf framework
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
        MavenArtifactUrlReference karafUrl = maven().groupId("org.bitbybit").artifactId("org.bitbybit.karaf.assembly")
                .versionAsInProject().type("zip");
        MavenUrlReference karafStandardRepo = maven().groupId("org.bitbybit").artifactId("org.bitbybit.demo.feature")
                .classifier("features").type("xml").versionAsInProject();
        return new Option[] {
                //uncomment next line to enable debugging
//                KarafDistributionOption.debugConfiguration("5005", true),
                karafDistributionConfiguration().frameworkUrl(karafUrl).unpackDirectory(new File("target/exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                KarafDistributionOption.features(karafStandardRepo, "osgi-demo-api", "osgi-demo-consumer", "osgi-demo-producer"),
        };
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

