## Installation

When installing it on a server, copy the file WEB-INF/lib/postgresql-9.3-1102-jdbc41.jar to the tomcat lib directory
(/usr/share/tomcat7/lib/ on Ubuntu)

If you want to store the database configuration details on the server, copy the file META-INF/context.xml to
/etc/tocmcat7/Catalina/localhost/ROOT.xml (if the application is deployed as
ROOT.war, otherwise change the xml name accordingly) and change the values in the ROOT.xml file to the real values for
the server.
Otherwise the values of the META-INF/context.xml file are taken.