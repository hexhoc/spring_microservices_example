package com.optimagrowth.license.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.optimagrowth.license.model.Organization;

@Component
public class OrganizationRestTemplateClient {

    // The Load Balancer–enabled RestTemplate class parses the URL passed into it and
    // uses whatever is passed in as the server name as the key to query the Load Balancer for
    // an instance of a service
    private RestTemplate restTemplate;

    @Autowired
    public OrganizationRestTemplateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Organization getOrganization(String organizationId){
        // When using a Load Balancer backed RestTemplate, builds the target URL
        // with the Eureka service ID
        ResponseEntity<Organization> restExchange =
                // The server name in the URL matches the application ID of the organization service
                // key that you used to register the organization service with Eureka:
                // http://{applicationid}/v1/organization/{organizationId}
                restTemplate.exchange(
                        "http://organization-service/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}