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

development step by step

1. Create new module "license server" with dependencies:
    - Actuator
    - Web
    - lombok
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
6. 