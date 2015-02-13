# osgi-itests-example
Example project containing a simple Pax Exam test configuration for Apache Felix and Apache Karaf.

## Building
  * Requires Java 8
  * Requires Maven 3+
  * Run "mvn install" from the project's root directory

## Project structure

This project consists of 8 modules and a root pom. The modules are:
* consumer-api : bundle containing the interface for the Consumer
* producer-api : bundle containing the interface for the Producer
* consumer : bundle containing the implementation of the Consumer interface and related blueprint service configuration
* producer : bundle containing the implementation of the Producer interface and related blueprint service configuration
* felix-itests : contains a Pax Exam integration test for the above bundles, running on Apache Felix, an implementation of the OSGi framework.
* feature: Apache Karaf archive, containing feature definitions of our consumer/producer/api bundles for Karaf. Karaf is a lightweight OSGi based runtime (it extends an underlying OSGi implementation such as Felix) that offers many useful features. One of these features is the ability to group bundles into features, allowing them to be installed/managed at the same time.
* karaf-assembly : a custom karaf distribution that uses the features defined in feature to start our bundles. The karaf distribution is packaged into a .zip archive and can then be either extracted and started normally (run ./bin/karaf within extracted archive folder) or used elsewhere, such as the karaf-itests bundle (which depends on karaf-assembly).
* karaf-itests: uses the karaf-assembly and feature modules to run the Pax Exam integration test used in felix-itests. Be aware that the configuration of tests for karaf is different than the configuration applied in the felix itest. 
