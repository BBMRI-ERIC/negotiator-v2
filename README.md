# Negotiator
![build](https://github.com/BBMRI-ERIC/negotiator-v2/actions/workflows/build.yml/badge.svg?)
[![codecov](https://codecov.io/github/BBMRI-ERIC/negotiator-v2/branch/feature/github_actions/graph/badge.svg?token=8W0I985ZXI)](https://codecov.io/github/BBMRI-ERIC/negotiator-v2)
![Latest Release](https://img.shields.io/github/v/release/bbmri-eric/negotiator-v2)
![Docker Image Size (latest by date)](https://img.shields.io/docker/image-size/bbmrieric/negotiator)


## Getting Started
The simplest way to spin up a negotiator instance is using docker. The commands bellow will start an instance with test
data and the authentication disabled:

`docker network create negotiator`

`docker run --name negotiator-db --network negotiator -p 5432:5432 -e POSTGRES_PASSWORD=negotiator -e POSTGRES_USER=negotiator -e POSTGRES_DB=negotiator -d postgres:14`

`docker run -d --name negotiator --network negotiator -p 8080:8080 -e POSTGRES_HOST="negotiator-db" -e AUTH="true"  bbmrieric/negotiator`
## Development mode

By settings the runtime property `de.samply.development.authenticationDisabled` to `true` in the bbmri.negotiator.xml,
 the application is started in development mode, that means:

- You don't need to be authenticated from PERUN (our identity provider) to use the application. You can just select one
  of the two roles at the login screen.
  
- You can run the application without connecting it with a directory. This would mean that you can not make/edit queries. 
  However, you can still work with dummy queries given in the dummy data.   
  
- You can create dummy data by setting `de.samply.development.deployDummyData` to `true`(Also in the bbmri.negotiator.xml).

Now if the application is started; after creating the database, the dummy data would be added to the database. 

The directory synchronization task fails in this case(because there is no connection to the directory) but that should 
not effect working of the negotiator. The directory synchronization can also be switched off by commenting out the 
following line from de.samply.bbmri.negotiator.listener.ServletListner.java

`timer.schedule(new DirectorySynchronizeTask(), 10000, 1000 * 60 * 60);` 


## Installation

When installing it on a server, copy the file WEB-INF/lib/postgresql-9.3-1102-jdbc41.jar to the tomcat lib directory 
(/usr/share/tomcat7/lib/ on Ubuntu)

If you want to store the database configuration details on the server, copy the file META-INF/context.xml to 
/etc/tocmcat7/Catalina/localhost/ROOT.xml (if the application is deployed as 
ROOT.war, otherwise change the xml name accordingly) and change the values in the ROOT.xml file to the real values for 
the server.
Otherwise the values of the META-INF/context.xml file are taken.


## Code generation with jooq



You can then generate the classes by running the following command:


```
mvn org.jooq:jooq-codegen-maven:generate
```
