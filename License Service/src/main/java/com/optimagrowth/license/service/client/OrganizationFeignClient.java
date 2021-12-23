package com.optimagrowth.license.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.optimagrowth.license.model.Organization;

// An alternative to the Spring Load Balancer–enabled RestTemplate class is Netflix’s Feign client library.
// Feign – a declarative HTTP client developed by Netflix. Feign aims at simplifying HTTP API clients. Simply put, the
// developer needs only to declare and annotate an interface while the actual implementation is provisioned at runtime
@FeignClient("organization-service")
public interface OrganizationFeignClient {
    // Defines the path and action to your endpoint
    @RequestMapping(
            method= RequestMethod.GET,
            value="/v1/organization/{organizationId}",
            consumes="application/json")
    // Defines the parameters passed into the endpoint
    Organization getOrganization(@PathVariable("organizationId") String organizationId);
}