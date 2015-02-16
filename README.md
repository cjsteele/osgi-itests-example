# osgi-itests-example
Example project containing a simple Pax Exam test configuration for Apache Felix and Apache Karaf.

## Building
  * Requires Java 8
  * Requires Maven 3+
  * Run "mvn install" from the project's root directory

## Versions
* OSGi 5.0.0
* Karaf 3.0.2

## Project structure

This project consists of 8 modules and a root pom. The modules are:
* **consumer-api** : bundle containing the interface for the Consumer
* **producer-api** : bundle containing the interface for the Producer
* **consumer** : bundle containing the implementation of the Consumer interface and related Aries Blueprint service configuration. Blueprint is a dependency injection framework for OSGi and is configured with an XML file located in the /src/main/resources/OSGI-INF/blueprint/ directory of a project (the name of the file is irrelevant). 
* **producer** : bundle containing the implementation of the Producer interface and related Blueprint service configuration
* **felix-itests** : contains a Pax Exam integration test for the above bundles, running on Apache Felix, an implementation of the OSGi framework.
* **feature**: Apache Karaf archive, containing feature definitions of our consumer/producer/api bundles for Karaf. Karaf is a lightweight OSGi based runtime (it extends an underlying OSGi implementation such as Felix) that offers many useful features. One of these features is the ability to group bundles into features, allowing them to be installed/managed at the same time. In order to use a feature, karaf requires a features descriptor, which is defined in src/main/filtered-resources/features.xml .
* **karaf-assembly** : a custom karaf distribution that uses the features defined in feature to start our bundles. The karaf distribution is packaged into a .zip archive and can then be either extracted and started normally (run ./bin/karaf within extracted archive folder) or used elsewhere, such as the karaf-itests bundle (which depends on karaf-assembly).
* **karaf-itests**: uses the karaf-assembly and feature modules to run the Pax Exam integration test used in felix-itests. Be aware that the configuration of tests for karaf is different than the configuration applied in the felix itest. 

## Test configuration

### OSGi test configuration

Pax Exam allows for the configuration of tests by using a method annotated with @Configuration. This method returns a list of Options, representing provisioning or property configuration options. A provisioning option instructs Pax Exam to load one or more bundles for the test class, these bundles can be specified using mavenBundle() to load them directly from a maven repository as shown below. Property options allow for the setting of system properties such as bundle/service loading timeouts. More on configuration options can be found at the [Pax Exam confluence site](https://ops4j1.jira.com/wiki/display/PAXEXAM4/Configuration+Options). An example of a test class configuration for OSGi and Pax Exam is shown below. The use of versionAsInProject() requires the depends-maven-plugin.

```
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
```

### Karaf test configuration

The example below shows how the custom karaf assembly is used to configure a Pax Exam test, as well as the use of karaf's features to define which bundles to load. In this case, the feature definitions in the "feature" module are used to load the required bundles. It seems a bit pointless in this example, as our bundles are very small and simple, but this approach has advantages when working with larger projects. OSGi applications are meant to be modular, so more complex applications can consist of many bundles that depend on each other. Using features, these bundles can all be installed at once (after producing an xml configuration file once). The features can be configured in their own maven module and re-used extensively.

```
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
```
