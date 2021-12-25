package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import com.optimagrowth.license.service.impl.LicenseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// Tells Spring bot that this is a REST based service,
// and it will automatically serialize/deserialize service request/response via JSON.
// Unlike the traditional Spring @Controller annotation, @RestController doesn’t require you
// to return a ResponseBody class from your method in the controller class.
@RestController
// Establish a versioning scheme for URL. The URL and its corresponding endpoint represent contract between the service owner and the consumer of the service
// One common patter is to prepend all endpoints with a version number (v1)
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {

    private static final Logger logger = LoggerFactory.getLogger(LicenseController.class);
    private final LicenseService licenseService;

    @Autowired
    public LicenseController(LicenseServiceImpl licenseService) {
        this.licenseService = licenseService;
    }

    //Get method to retrieve the license data
    @GetMapping(value = "/{licenseId}/{clientType}")
    //Maps two parameters (organizationId and licenseId) from URL to @GetMapping's parameters
    public ResponseEntity<License> getLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId,
            @PathVariable(value = "clientType", required = false) String clientType,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        License license = licenseService.getLicense(licenseId, organizationId, clientType, locale);

        //HATEOAS
        license.add(
                linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId(),clientType, locale)).withSelfRel(),
                linkTo(methodOn(LicenseController.class).createLicense(license)).withRel("createLicense"),
                linkTo(methodOn(LicenseController.class).updateLicense(license)).withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class).deleteLicense(license.getLicenseId(), locale)).withRel("deleteLicense"));

        logger.info("Method get license by organization id {} and license id {} and client type {}",organizationId, licenseId, clientType);

//        The ResponseEntity represents the entire HTTP response, including the status code, the headers, and the
//        body. In the previous listing, it allows us to return the License object as the body and
//        the 200(OK) status code as the HTTP response of the service.
        return ResponseEntity.ok(license);
    }

    @PutMapping
    public ResponseEntity<License> updateLicense(
            //Maps the HTTP request body to a License object
            @RequestBody License license) {

        logger.info("Method update license");

        return ResponseEntity.ok(licenseService.updateLicense(license));
    }

    @PostMapping
    public ResponseEntity<License> createLicense(
            @RequestBody License license) {

        logger.info("Method create license");

        return ResponseEntity.ok(licenseService.createLicense(license));
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("licenseId") String licenseId,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        logger.info("Method delete license by license id {}", licenseId);

        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, locale));
    }

    @RequestMapping(value="/",method = RequestMethod.GET)
    public List<License> getLicenses(@PathVariable("organizationId") String organizationId) {
        logger.info("Method get licenses by organization id: {}", organizationId);
        return licenseService.getLicensesByOrganization(organizationId);
    }


}
