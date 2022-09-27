FROM tomcat:8.5.82-jre17
MAINTAINER RadoT
RUN apt update && apt install nano
RUN mkdir /etc/bbmri.negotiator /opt/negotiator
RUN rm -fr /usr/local/tomcat/webapps/ROOT
RUN rm -fr /usr/local/tomcat/webapps/conf/context.xml
ADD target/bbmri-negotiator-2.1.3-SNAPSHOT/META-INF/context.xml           ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml
COPY src/main/resources/bbmri.negotiator.xml /etc/bbmri.negotiator
COPY src/main/resources/log4j.properties /etc/bbmri.negotiator
COPY target/bbmri-negotiator-2.1.3-SNAPSHOT.war /usr/local/tomcat/webapps
RUN mv /usr/local/tomcat/webapps/bbmri-negotiator-2.1.3-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
COPY target/bbmri-negotiator-2.1.3-SNAPSHOT/META-INF/context.xml /usr/local/tomcat/webapps/conf/Catalina/localhost/
RUN mkdir /docker
ADD docker/start.sh                         /docker/
RUN chmod +x                                    /docker/start.sh
RUN chown -R 1001:1001 /docker /etc/bbmri.negotiator /opt/negotiator /usr/local/tomcat/
USER 1001
CMD ["sh", "-c", "/docker/start.sh"]
