package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
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
    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    //Get method to retrieve the license data
    @GetMapping(value = "/{licenseId}")
    //Maps two parameters (organizationId and licenseId) from URL to @GetMapping's parameters
    public ResponseEntity<License> getLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId) {

        License license = licenseService.getLicense(licenseId, organizationId);

        //HATEOAS
        license.add(linkTo(methodOn(LicenseController.class)
                        .getLicense(organizationId, license.getLicenseId()))
                        .withSelfRel(),
                linkTo(methodOn(LicenseController.class)
                        .createLicense(organizationId, license, null))
                        .withRel("createLicense"),
                linkTo(methodOn(LicenseController.class)
                        .updateLicense(organizationId, license, null))
                        .withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class)
                        .deleteLicense(organizationId, license.getLicenseId(), null))
                        .withRel("deleteLicense"));

//        The ResponseEntity represents the entire HTTP response, including the status code, the headers, and the
//        body. In the previous listing, it allows us to return the License object as the body and
//        the 200(OK) status code as the HTTP response of the service.
        return ResponseEntity.ok(license);
    }

    @PutMapping
    public ResponseEntity<String> updateLicense(
            @PathVariable("organizationId") String organizationId,
            //Maps the HTTP request body to a License object
            @RequestBody License request,
            @RequestHeader(value = "Accept-Language", required = false) Locale local) {

        return ResponseEntity.ok(licenseService.updateLicense(request, organizationId, local));
    }

    @PostMapping
    public ResponseEntity<String> createLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License request,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        return ResponseEntity.ok(licenseService.createLicense(request, organizationId, locale));
    }

    @DeleteMapping(value = "/{licenseId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId, locale));
    }

}
