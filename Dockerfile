FROM tomcat:8.5.82-jre17
MAINTAINER RadoT
RUN chown 1001:1001 /usr/local/tomcat/webapps/
COPY target/bbmri-negotiator-2.1.3-SNAPSHOT.war /usr/local/tomcat/webapps/
USER 1001
