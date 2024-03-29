# Development
## Database

To run the application in development mode you first need to spin up a database. We currently support postgres:14. The simplest way is to create a postgres docker container with default credentials for the application:
```sh
docker run --name negotiator-db --network negotiator -p 5432:5432 -e POSTGRES_PASSWORD=negotiator -e POSTGRES_USER=negotiator -e POSTGRES_DB=negotiator -d postgres:14
```


## Application
For the next step please rename the [bbmri.negotiator.example.xml](src/main/resources/bbmri.negotiator.example.xml) in _**src/main/resources**_ to: src/main/resources/bbmri.negotiator.xml


By settings the runtime property `de.samply.development.authenticationDisabled` to `true` in the [bbmri.negotiator.xml](src/main/resources/bbmri.negotiator.xml),
the application is started in development mode, that means:

- You don't need to be authenticated from [PERUN](https://perun-aai.org/) (our identity provider) to use the application. You can just select one
  of the roles at the login screen.

- You can run the application without connecting it to a directory. This would mean that you can not make/edit queries.
  However, you can still work with dummy queries given in the dummy data.

- You can create dummy data by setting `de.samply.development.deployDummyData` to `true`(Also in the bbmri.negotiator.xml).

Now if the application is started; after creating the database, the dummy data would be added to the database.

The directory synchronization task fails in this case(because there is no connection to the directory) but that should
not affect working of the negotiator. The directory synchronization can also be switched off by commenting out the
following line from de.samply.bbmri.negotiator.listener.ServletListner.java

`timer.schedule(new DirectorySynchronizeTask(), 10000, 1000 * 60 * 60);`

## [Maven](https://maven.apache.org/)
We use maven as our project management tool. So if you don't have it already installed please do so and verify that everything was installed correctly by running:

`mvn version`

NOTE: The **_.mvn/settings.xml_** file is for adding a mirror to download our legacy libraries from an internal nexus.

Now to create the war package simply run the following command in the project directory:

`mvn clean package`

To deploy the WAR file to Tomcat please follow this **[guide](https://www.baeldung.com/tomcat-deploy-war)** or run it using tomcat configuration in InteliJ Ultimate as decribed [here](https://www.jetbrains.com/idea/guide/tutorials/working-with-apache-tomcat/using-existing-application/).

## Testing

For acceptance tests we use the [Selenium IDE](https://www.selenium.dev/selenium-ide/) to generate high level and human-readable tests.
If you would like to add some please export them to python (Click export test suite, select format) and copy them to _**.github/tests/test_use-cases.py**_

## Code generation with jooq

You can then generate the classes for new a database schema by running the following command:

```
mvn org.jooq:jooq-codegen-maven:generate
```

Additional documentation:
* [Database schema](src/main/resources/db/migration/negotiator/README.md)
