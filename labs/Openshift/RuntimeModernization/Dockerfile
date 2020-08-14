## Build stage
FROM maven:latest AS builder
COPY app/ /
RUN cd CustomerOrderServicesProject && mvn clean package

## Application image
FROM openliberty/open-liberty:full-java8-openj9-ubi

COPY --chown=1001:0 resources/ /opt/ol/wlp/usr/shared/resources/

COPY --chown=1001:0 config/server.xml /config/server.xml

COPY --chown=1001:0 config/configDropins/overrides/*.xml /config/configDropins/overrides/

COPY --from=builder --chown=1001:0 CustomerOrderServicesApp/target/CustomerOrderServicesApp-0.1.0-SNAPSHOT.ear /config/apps/CustomerOrderServicesApp.ear

RUN configure.sh
