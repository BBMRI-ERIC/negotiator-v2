FROM tomcat:8.5.82-jre17-temurin-focal
MAINTAINER RadoT
RUN apt update && apt install nano
RUN mkdir /etc/bbmri.negotiator /opt/negotiator
RUN rm -fr /usr/local/tomcat/webapps/ROOT
RUN rm -fr /usr/local/tomcat/webapps/conf/context.xml
ADD src/main/webapp/META-INF/context.xml          ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml
ADD docker/bbmri.negotiator.xml /etc/bbmri.negotiator
ADD src/main/resources/log4j.properties /etc/bbmri.negotiator
ADD target/negotiator.war /usr/local/tomcat/webapps
RUN mv /usr/local/tomcat/webapps/negotiator.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir /docker
ADD docker/start.sh                         /docker/
RUN chmod +x                                    /docker/start.sh
RUN chown -R 1001:1001 /docker /etc/bbmri.negotiator /opt/negotiator /usr/local/tomcat/
USER 1001
CMD ["sh", "-c", "/docker/start.sh"]
