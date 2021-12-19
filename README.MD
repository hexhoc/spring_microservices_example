# Spring microservices example

This example demonstrate how we can build microservice project step by step.

### We use fictitious company **Optima Growth**

Optima Growth is a software development company whose core product, Optima Stock (that we’ll refer to as O-stock),
provides an enterprise-grade asset management application. It furnishes coverage for all the critical elements:
inventory, software delivery, license management, compliance, cost, and resource management. Its pri- mary goal is to
enable organizations to gain an accurate point-in-time picture of their software assets. The company is approximately 12
years old. The company wants to rebuild their core product, O-stock. While much of the business logic for the
application will remain in place, the application itself will be broken down from a monolithic architecture to a much
smaller microservice design, whose pieces can be deployed independently to the cloud. The replatforming involved with
O-stock can be a “make or break” moment for the company.

## development step by step
### license service
1. Create new module "license server" with dependencies:
    - Actuator
    - Web
    - lombok
    - HATEOAS (this service return link how we can work with resource (GET, CREATE, UPDATE, DELETE))
    - spring-cloud-starter-config (client for work with cloud config server)
    - spring-boot-starter-data-jpa
    - postgresql
    - spring-cloud-starter-bootstrap (for use bootstrap.properties)
2. Create class com/optimagrowth/license/controller/LicenseController.java with @RestController and @RequestMapping(
   value = "v1/organization/{organizationId}/license")

   - API:
       - v1/organization/{organizationId}/license/{licenseId} - GET license
       - v1/organization/{organizationId}/license/ - PUT update license 
       - v1/organization/{organizationId}/license/ - POST create new license
       - v1/organization/{organizationId}/license/{licenseId} - DELETE license 
3. Create model class com/optimagrowth/license/model/License.java. Simple POJO
4. Create service class com/optimagrowth/license/service/LicenseService.java
5. Create bean to work with our application in different language:
   1. LocaleResolver localeResolver()
   2. ResourceBundleMessageSource messageSource()
   3. Create files messages_en.properties and messages_ru.properties in resource folder
   4. Inject MessageSource in LicenseService and use it 
6. Implementing Spring HATEOAS to display related links
7. The DevOps story: Building for the rigors of runtime
   1. Service assembly: Packaging and deploying your microservices.
   
      `./gradlew assemble && java -jar build/libs/licensing-service-0.0.1-SNAPSHOT.jar` 
   2. Create dockerfile
   
      `FROM openjdk:17
      ARG JAR_FILE=build/libs/*.jar
      COPY ${JAR_FILE} app.jar
      ENTRYPOINT ["java","-jar","/app.jar"]`
      
      or using gradle

      `./gradlew bootBuildImage --imageName=ostock/licensing-service:0.0.1-SNAPSHOT`
   3. Pack our project in docker image and try to start
   
      `docker build --build-arg JAR_FILE=build/libs/*.jar -t ostock/licensing-service:0.0.1-SNAPSHOT .`
      
      `docker run --name licensing-service -p 8080:8080 ostock/licensing-service:0.0.1-SNAPSHOT`
   
   4. Create docker-compose.yml and try to start it `docker-compose up`
8. Create default application.properties in which to specify connect to config server and active profile
9. We can compile jar with override properties using next command:
   `java -Dspring.cloud.config.uri=http://localhost:8071
   -Dspring.profiles.active=dev
   -jar target/licensing-service-0.0.1-SNAPSHOT.jar`
10. You can check all environment variables through actuator `http://localhost:8080/actuator/env`.


### Config server

1. Create new spring boot application (config server) with following dependencies:
   1. Actuator
   2. Config server
   3. spring-cloud-starter-bootstrap
2. Create bootstrap.properties
3. Add @EnableConfigServer annotation in ConfigurationServerApplication class
4. Create config for licensing-service:
   1. licensing-service.properties
   2. licensing-service-dev.properties
   3. licensing-service-prod.properties

   Spring framework implements a hierarchical mechanism for properties. First applied licensing-service.properties and
   after then licensing-service-dev.properties
5. Create Dockerfile and edit docker-compose.yml for new image
6. Check it service is work `http://localhost:8071/licensing-service/default`