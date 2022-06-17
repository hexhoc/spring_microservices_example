package com.optimagrowth.license.service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.optimagrowth.license.model.Organization;

@Component
// In real life You should only use the Discovery Client when your service needs to query the Load
// Balancer to understand what services and service instances are registered with it.
// In this example:
// 1. You aren't taking advantage of the Spring Cloud client-side Load Balancer. By
//  calling the Discovery Client directly, you get a list of services, but it becomes
//  your responsibility to choose which returned service instance you’re going to invoke
// 2. You’re doing too much work. In the code, you have to build the URL that you’ll
//  use to call your service. It’s a small thing, but every piece of code that you
//  can avoid writing is one less piece of code that you have to debug.
public class OrganizationDiscoveryClient {

    //DiscoveryClient class. You use this class to interact with the Spring Cloud Load Balancer
    private DiscoveryClient discoveryClient;

    @Autowired
    public OrganizationDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public Organization getOrganization(String organizationId) {

        //Demonstrate how to create restTemplate without autowired
        RestTemplate restTemplate = new RestTemplate();
        // Gets a list of all the instances of the organization services
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

        if (instances.size()==0) return null;
        // Retrieves the service endpoint
        String serviceUri = String.format("%s/v1/organization/%s",instances.get(0).getUri().toString(), organizationId);

        // Once you have a target URL, you can use a standard Spring RestTemplate to call your organization service
        // and retrieve the data.
        ResponseEntity< Organization > restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}