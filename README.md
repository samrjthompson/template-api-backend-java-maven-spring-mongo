# template-api-backend-java-maven-spring-mongo
This is a work in progress and may contain bloat. The project is designed to make starting an API project quicker.

## Default settings
You will need to change a few defaults for the project to work correctly.
1. The _**group id**_ for Maven needs to change to something appropriate. This needs changing in both the pom and packages (main and test).
2. The _**artifact id**_ will also need to change in the pom.
3. The _**url for the MongoDB connection**_ needs to change - follow instructions on the [Mongo Atlas website](https://account.mongodb.com/account/login).
4. Spring security is disabled by default - this will need to be added to the pom.
5. Dependency versions will not necessarily be up-to-date - ensure this is reviewed when starting a new project by viewing the [Maven Repo](https://mvnrepository.com/).