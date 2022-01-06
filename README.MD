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

## Remote debug:
1. configserver. Port 9071
2. eurekaserver. Port 9070
3. gatewayserver. Port 9072
4. licensingservice. Port 9180
5. organizationservice. Port 9081


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
   - flyway. Flyway migration. Not need additional configuration, only enable it in spring properties file. Spring
     automatically found migration file and execute it. You do not need flyway plugin!
   - resilience4j-spring-boot2. We can test it if try to
     connect http://localhost:8180/v1/organization/d898a142-de44-466c-8c88-9ceb2c2429d3/license/ a few times
   - spring-boot-starter-aop. It is needed for resilience4j

2. Create class com/optimagrowth/license/controller/LicenseController.java with @RestController and @RequestMapping(
   value = "v1/organization/{organizationId}/license")

    - API:
       - v1/organization/{organizationId}/license/{licenseId} - GET license
       - v1/organization/{organizationId}/license/ - PUT update license 
     - v1/organization/{organizationId}/license/ - POST create new license
     - v1/organization/{organizationId}/license/{licenseId} - DELETE license
3. Create model class com/optimagrowth/license/model/License.java.
4. Create service class com/optimagrowth/license/service/LicenseService.java
5. Create config class that can read property from application.properties and persist it into instance of the class. Use
   annotation @ConfigurationProperties(prefix = "example")
6. Create bean to work with our application in different language:
   1. LocaleResolver localeResolver()
   2. ResourceBundleMessageSource messageSource()
   3. Create files messages_en.properties and messages_ru.properties in resource folder
   4. Inject MessageSource in LicenseService and use it 
7. Add @RefreshScope in LicenseServiceApplication class.

   Spring Boot Actuator offers a @RefreshScope annotation that allows a development team to access a /refresh endpoint
   that will force the Spring Boot application to reread its application configuration.

8. Implementing Spring HATEOAS to display related links
9. The DevOps story: Building for the rigors of runtime
   1. Service assembly: Packaging and deploying your microservices.
   
   ```
   ./gradlew assemble && java -jar build/libs/licensing-service-0.0.1-SNAPSHOT.jar
   ``` 
   3. Create dockerfile

   ```
   FROM openjdk:17
   ARG JAR_FILE=build/libs/*.jar
   COPY ${JAR_FILE} app.jar
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

      or using gradle

   ```
   ./gradlew bootBuildImage --imageName=ostock/licensing-service:0.0.1-SNAPSHOT
   ```
   4. Pack our project in docker image and try to start

   ```
   docker build --build-arg JAR_FILE=build/libs/*.jar -t ostock/licensing-service:0.0.1-SNAPSHOT .
   ```
      
   ```
   docker run --name licensing-service -p 8180:8180 ostock/licensing-service:0.0.1-SNAPSHOT
   ```
   
    5. Create docker-compose.yml and try to start it ```docker-compose up```
10. Create default application.properties in which to specify connect to config server and active profile
11. We can compile jar with override properties using next command:
    ```
    java -Dspring.cloud.config.uri=http://localhost:8071
    -Dspring.profiles.active=dev
    -jar target/licensing-service-0.0.1-SNAPSHOT.jar
    ```
12. You can check all environment variables through actuator
   ```
   http://localhost:8180/actuator/env
   ```
13. Add db/migration in resource and enable spring.flyway in properties

### license service. Handle exception
1. Add ExceptionController class. You can handle exceptions globally and centrally using classes annotated with @ControllerAdvice.
2. Add ErrorMessage class. Instance of that class can contain our error with code, status and message
3. Add RestErrorList class. This class extend ArrayList and contains list of ErrorMessage
4. Add ResponseWrapper Class to wrap RestErrorList

### Organization service
1. Create new spring boot application (Organization service) with following dependencies:
   - spring-boot-starter-actuator
   - spring-cloud-starter-bootstrap
   - spring-boot-starter-data-jpa
   - spring-boot-starter-web
   - spring-cloud-starter-config
   - lombok
   - postgresql


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
    
   And put it in git repository in config folder
   Spring framework implements a hierarchical mechanism for properties. First applied licensing-service.properties and
   after then licensing-service-dev.properties
5. Create Dockerfile and edit docker-compose.yml for new image
6. Check it service is work 
   ```
   http://localhost:8071/licensing-service/default
   ```
7. Add encrypt symmetric key in bootstrap.properties. The symmetric encryption key is nothing more than a shared secret
   that’s used by the encrypter to encrypt a value and by the decrypter to decrypt a value.

   Now we can use http://localhost:8071/encrypt and http://localhost:8071/decrypt for our passwords

### Eureka server (discovery server)

1. Add new module spring boot with following dependencies:
   - spring-boot-starter-web
   - spring-cloud-starter-config
   - spring-cloud-starter-loadbalancer
   - spring-cloud-starter-netflix-eureka-server
   - spring-cloud-starter-bootstrap
2. Add @EnableEurekaServer annotation in EurekaServerApplication class
3. Check http://localhost:8070/eureka/apps/organization-service

### API Gateway server
1. Add dependencies:
   - spring-cloud-starter-bootstrap
   - spring-boot-starter-actuator
   - spring-cloud-starter-config
   - spring-cloud-starter-gateway
   - spring-cloud-starter-netflix-eureka-client
2. Change access to organization-service and licensing-service
   - You can check new routes by address http://localhost:8072/actuator/gateway/routes

## Security

### Security. KeyClock
1. Create new docker container for keyclock image
2. Open localhost:8080, login and password "admin" and config new realm "spmia-realm"
3. Add frontend URL - http://keycloak:8080/auth
4. Create new client "ostock". Access Type = confidential, Service Accounts Enabled = ON, Authorization Enabled = ON
5. Create two client's roles - USER and ADMIN (not composite)
6. Create two realm's roles - ostock-admin and ostock-user (composite) relative with client's roles
7. Create new user, login = hexhoc, password = Vcsdfr13, role = ostock_admin
8. Create POST query for get access token. Basic auth - username = ostock, password = QMCX3PjFa9nrVObDSi12ISH790vWRfO4 (
   client's credentials) and body - grant_type = password, username = hexhoc, password = Vcsdfr13

   ```
   curl --location --request POST 'http://localhost:8080/auth/realms/spmia-realm/protocol/openid-connect/token' \
   --header 'Authorization: Basic b3N0b2NrOlFNQ1gzUGpGYTluclZPYkRTaTEySVNINzkwdldSZk80' \
   --header 'Content-Type: application/x-www-form-urlencoded' \
   --data-urlencode 'grant_type=password' \
   --data-urlencode 'username=hexhoc' \
   --data-urlencode 'password=Vcsdfr13'
   ```

### Security. Organization-service
1. Add a few dependencies:
   - keycloak-spring-boot-starter
   - spring-boot-starter-security
2. Add configuration properties (connect, rules, etc) for KeyClock
3. Create SpringConfig class that extend KeycloakWebSecurityConfigurerAdapter. This class configure organizaton-service
   how it should secure resource
4. Add @RolesAllowed annotation in OrganizationController class

### Security. License-service

1. Add a few dependencies:
   - keycloak-spring-boot-starter
   - spring-boot-starter-security
2. Add configuration properties (connect, rules, etc) for KeyClock
3. Create SpringConfig class that extend KeycloakWebSecurityConfigurerAdapter. This class configure licensing-service
   how it should secure resource
4. Create @Bean KeycloakRestTemplate. Keycloak provides a new REST template class that support - 
   - Grab the HTTP header of the incoming licensing service call
   - Add it to every outbound service call in the licensing service
5. When we use RestTemplate to get data from another service, we should do it through gateway - http://gateway-server:8072
   or http://localhost:8072 (to dev profile).

### Security. gatewayservice

1. Add dependency - json
2. Add in FilterUtils class method that extract and return Auth token from headers
3. Add in TrackingFilter class method getUsername that decode auth token and get Username from it
4. Change routes in configuration properties. By default, the gateway doesn’t forward sensitive HTTP headers like cookie,
   set-cookie, and authorization to downstream services

### Security. How it works
Pay attention! we should not use any interceptors to intercept and distribute the user token. This whole key-cloak makes itself.
When some service get token, it itself going to the KeyCloak service and validate it.

1. User somehow get token from KeyClock! TODO
2. Call licensing service with token through gateway
3. The gateway looks up the licensing service endpoint and then forwards the call
   to one of the licensing service’s servers. The services gateway copies the
   authorization HTTP header from the incoming call and ensures that the HTTP
   header is forwarded on to the new endpoint.

   **This is work automatically. We do not need to do something for that.**
4. The licensing service receives the incoming call. Because the licensing service is
   a protected resource, the licensing service will validate the token with the Keycloak server and then check the 
   user’s roles for the appropriate permissions. As part of its work, the licensing service invokes the organization service.
   When doing this, the licensing service needs to propagate the user’s access
   token to the organization service.

### Security. Recommendations
For production use, you should also build your microservice security around the following practices:
   - Use HTTPS/Secure Sockets Layer (SSL) for all service communications.
   - Use an API gateway for all service calls.
   - Provide zones for your services (for example, a public API and private API).
   - Limit the attack surface of your microservices by locking down unneeded network ports

## Message broker
### Config kafka in organization service
Add docker images:
   - kafka
   - zookeeper
   - redis

**Modify the organization service to _publish_ a message to Kafka every time the organization service changes data**
1. Add dependencies:
   - spring-cloud-stream
   - spring-cloud-starter-stream-kafka
2. @EnableBinding(Source.class) in OrganizationServiceApplication class
3. Add configuration properties
   - Channel for communication
   - Content-type
   - Bind with kafka
4. Create OrganizationChangeModel class. This class is DTO for kafka with following fields:
   - type
   - action
   - organizationId
   - correlationId
5. Create SimpleSourceBean class. This class will implement **publishOrganizationChange** method this method get data
   about changes create **OrganizationChangeModel** instance and send it to message broker (kafka)
6. Add ThreadLocal static variables to UserContext class, to store data individually for the current thread.

### Config kafka in licensing service
1. Add dependencies:
   - spring-cloud-stream
   - spring-cloud-starter-stream-kafka
   - jedis. Spring uses the Jedis open source project to communicate with a Redis server
   - spring-data-redis
2. Add configuration properties
   - Channel for communication
   - Content-type
   - Bind with kafka
3. Create OrganizationChangeModel class. This class is DTO for kafka with following fields:
   - type
   - action
   - organizationId
   - correlationId
4. Create OrganizationChangeHandler class to logging communication with kafka
5. Create CustomChannels class to custom channel for kafka
6. Add ThreadLocal static variables to UserContext class, to store data individually for the current thread.

### Config Redis in licensing service
We are work with redis like database, using entity and repository for implement CRUD model. 

1. Add dependencies:
   - jedis. Spring uses the Jedis open source project to communicate with a Redis server
   - spring-data-redis
2. Add configuration properties
   - Redis host and port
3. Create @Bean JedisConnectionFactory to connect to redis server and @Bean @RedisTemplate to using that connect to work with redis
4. Create interface repository OrganizationRedisRepository. This will be crud model for redis
5. Add @RedisHash("organization") to Organization class. Organization is entity that contains in redis hash
6. Edit OrganizationRestTemplateClient class to check redis cache before retrieve organization from organization-service




## Installation

1. Use for each project
   ```
   gradlew buildDockerImage
   ``` 
2. cd docker/ and execute 
   ```
   docker-compose up
   ``` 



