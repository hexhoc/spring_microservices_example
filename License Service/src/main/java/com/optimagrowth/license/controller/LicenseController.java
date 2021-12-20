package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import com.optimagrowth.license.service.impl.LicenseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private final LicenseService licenseService;

    @Autowired
    public LicenseController(LicenseServiceImpl licenseService) {
        this.licenseService = licenseService;
    }

    //Get method to retrieve the license data
    @GetMapping(value = "/{licenseId}")
    //Maps two parameters (organizationId and licenseId) from URL to @GetMapping's parameters
    public ResponseEntity<License> getLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        License license = licenseService.getLicense(licenseId, organizationId, locale);

        //HATEOAS
        license.add(
                linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId(), locale)).withSelfRel(),
                linkTo(methodOn(LicenseController.class).createLicense(license)).withRel("createLicense"),
                linkTo(methodOn(LicenseController.class).updateLicense(license)).withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class).deleteLicense(license.getLicenseId(), locale)).withRel("deleteLicense"));

//        The ResponseEntity represents the entire HTTP response, including the status code, the headers, and the
//        body. In the previous listing, it allows us to return the License object as the body and
//        the 200(OK) status code as the HTTP response of the service.
        return ResponseEntity.ok(license);
    }

    @PutMapping
    public ResponseEntity<License> updateLicense(
            //Maps the HTTP request body to a License object
            @RequestBody License license) {

        return ResponseEntity.ok(licenseService.updateLicense(license));
    }

    @PostMapping
    public ResponseEntity<License> createLicense(
            @RequestBody License license) {

        return ResponseEntity.ok(licenseService.createLicense(license));
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("licenseId") String licenseId,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, locale));
    }

}
