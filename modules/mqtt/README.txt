GridGain MQTT Module
-------------------------

GridGain MQTT module provides a streamer to consume MQTT topic messages into
GridGain caches.

Importing GridGain MQTT Module In Maven Project
----------------------------------------------------

If you are using Maven to manage dependencies of your project, you can add the MQTT module
dependency like this (replace '${ignite.version}' with actual GridGain version you are
interested in):

<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                        http://maven.apache.org/xsd/maven-4.0.0.xsd">
    ...
    <dependencies>
        ...
        <dependency>
            <groupId>org.gridgain</groupId>
            <artifactId>ignite-mqtt</artifactId>
            <version>${ignite.version}</version>
        </dependency>
        ...
    </dependencies>
    ...
</project>
