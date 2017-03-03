# Negotiator

This is the BBMRI negotiator

## Links (MITRO related)

- [Jira](https://jira.mitro.dkfz.de/secure/RapidBoard.jspa?rapidView=9&projectKey=BIO)
- [Bitbucket](https://code.mitro.dkfz.de/projects/BIO)
- [Confluence](https://wiki.mitro.dkfz.de/display/BIO/Biobank+Home)


## Development mode

By settings the runtime property `de.samply.development` to `true`, the application is started in development mode, that means:

- if the database is empty, the necessary tables **and the dummy data** are created.

You can set this property by opening the runtime configuration of your tomcat server and adding `-Dde.samply.development=true` to the VM arguments.


## Installation

When installing it on a server, copy the file WEB-INF/lib/postgresql-9.3-1102-jdbc41.jar to the tomcat lib directory 
(/usr/share/tomcat7/lib/ on Ubuntu)

If you want to store the database configuration details on the server, copy the file META-INF/context.xml to 
/etc/tomcat7/Catalina/localhost/ROOT.xml (if the application is deployed as 
ROOT.war, otherwise change the xml name accordingly) and change the values in the ROOT.xml file to the real values for 
the server.
Otherwise the values of the META-INF/context.xml file are taken.


## Code generation with jooq

Before you can generate the classes you should configure maven properly. In this case the properties
for the database, the username and the password for the code generation are taken from your profile. Edit your
`settings.xml` so that it looks like this:


```
...
  <profiles>
    <profile>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <negotiator.database>YOUR_NEGOTIATOR_DATABASE</negotiator.database>
        <negotiator.user>YOUR_NEGOTIATOR_DATABASE_USERNAME</negotiator.user>
        <negotiator.password>YOUR_NEGOTIATOR_DATABASE_PASSWORD</negotiator.password>
      </properties>

      ...

    </profile>
  </profiles>
...
```


You can then generate the classes by running the following command:


```
mvn org.jooq:jooq-codegen-maven:generate
```
