package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Tells Spring bot that this is a REST based service,
// and it will automatically serialize/deserialize service request/response via JSON.
// Unlike the traditional Spring @Controller annotation, @RestController doesn’t require you
// to return a ResponseBody class from your method in the controller class.
@RestController
// Establish a versioning scheme for URL. The URL and its corresponding endpoint represent contract between the service owner and the consumer of the service
// One common patter is to prepend all endpoints with a version number (v1)
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {

    private LicenseService licenseService;

    @Autowired
    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    //Get method to retrieve the license data
    @GetMapping(value = "/{licenseId}")
    //Maps two parameters (organizationId and licenseId) from URL to @GetMapping's parameters
    public ResponseEntity<License> getLicense(
            @PathVariable("organizationId") String organizationalId,
            @PathVariable("licenseId") String licenseId) {

        License license = licenseService.getLicense(licenseId, organizationalId);
//        The ResponseEntity represents the entire HTTP response, including the status code, the headers, and the
//        body. In the previous listing, it allows us to return the License object as the body and
//        the 200(OK) status code as the HTTP response of the service.
        return ResponseEntity.ok(license);
    }

    @PutMapping
    public ResponseEntity<String> updateLicense(
            @PathVariable("organizationId") String organizationId,
            //Maps the HTTP request body to a License object
            @RequestBody License request) {

        return ResponseEntity.ok(licenseService.updateLicense(request, organizationId));
    }

    @PostMapping
    public ResponseEntity<String> createLicense(
            @PathVariable("organizationalId") String organizationId,
            @RequestBody License request) {

        return ResponseEntity.ok(licenseService.createLicense(request, organizationId));
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId) {

        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId));
    }

}
