package com.optimagrowth.license.service.impl;


import java.util.*;
import java.util.concurrent.TimeoutException;

import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.service.LicenseService;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.repository.LicenseRepository;
import org.springframework.web.client.ResourceAccessException;

@Service
public class LicenseServiceImpl implements LicenseService {

    private MessageSource messages;
    private LicenseRepository licenseRepository;
    private ServiceConfig config;
    private OrganizationFeignClient organizationFeignClient;
    private OrganizationRestTemplateClient organizationRestClient;
    private OrganizationDiscoveryClient organizationDiscoveryClient;


    @Autowired
    public LicenseServiceImpl(MessageSource messages,
                              LicenseRepository licenseRepository,
                              ServiceConfig config,
                              OrganizationFeignClient organizationFeignClient,
                              OrganizationRestTemplateClient organizationRestClient,
                              OrganizationDiscoveryClient organizationDiscoveryClient) {
        this.messages = messages;
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationFeignClient = organizationFeignClient;
        this.organizationRestClient = organizationRestClient;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
    }

    @CircuitBreaker(name = "organizationService")
    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case "feign":
                // Feign—Uses Netflix’s Feign client library to invoke a service via the Load Balancer
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                // Rest—Uses an enhanced Spring RestTemplate to invoke the Load Balancer service
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                // Discovery—Uses the Discovery Client and a standard Spring RestTemplate class
                // to invoke the organization service
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }

    // it dynamically generates a proxy that wraps the method and manages all calls to that method through a thread pool
    // specifically set aside to handle remote calls.
    @CircuitBreaker(
            name = "licenseService",
            // if fallback is set up, then circuit breaker ring will not close
            fallbackMethod = "buildFallbackLicenseList")
    // retry pattern is responsible for retrying attempts to communicate with a service when that service initially fails
    @Retry(
            name = "retryLicenseService",
            fallbackMethod = "buildFallbackLicenseList/"
    )
    // Bulkhead wrap our request in thread pool (or use semaphore for current thread) check limit time for each request
    // If the time has expired, bulkhead use fallback method
    @Bulkhead(
            name = "bulkheadLicenseService",
            type = Bulkhead.Type.SEMAPHORE,
            fallbackMethod = "buildFallbackLicenseList")
    public List<License> getLicensesByOrganization(String organizationId) {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    public License getLicense(String licenseId, String organizationId, String clientType, Locale locale){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(String.format(messages.getMessage("license.search.error.message", null, locale),licenseId, organizationId));
        }

        Organization organization = retrieveOrganizationInfo(organizationId, clientType);
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    public License createLicense(License license){
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);

        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license){
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId, Locale locale){
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messages.getMessage("license.delete.message", null, locale),licenseId);
        return responseMessage;

    }

    // Methods to simulate long execute for circuit breaker
    private void randomlyRunLong(){
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        if (randomNum==3) {
            uncheckedSleep();
        }
    }

    private void uncheckedSleep(){
        try {
            Thread.sleep(2500);
            //We throw exception and circuit breaker catch this exception, and mark it.
            //We are say in bootstrap.properties, that we are record it of this exception is appeared
            throw new ResourceAccessException("Operation is timeout");
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    // Defines a single function that’s called if the calling service fails Returns a hardcoded value
    // in the fallback method
    private List<License> buildFallbackLicenseList(String organizationId, Throwable t){
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName(
                "Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }
}