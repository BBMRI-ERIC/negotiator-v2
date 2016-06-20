# Negotiator

This is the BBMRI negotiator

## Links (MITRO related)

- [Jira](https://jira.mitro.dkfz.de/secure/RapidBoard.jspa?rapidView=9&projectKey=BIO)
- [Bitbucket](https://code.mitro.dkfz.de/projects/BIO)
- [Confluence](https://wiki.mitro.dkfz.de/display/BIO/Biobank+Home)


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
