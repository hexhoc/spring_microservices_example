package com.optimagrowth.license.service.impl;


import java.util.Locale;
import java.util.UUID;

import com.optimagrowth.license.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.repository.LicenseRepository;

@Service
public class LicenseServiceImpl implements LicenseService {

    MessageSource messages;
    private LicenseRepository licenseRepository;
    ServiceConfig config;

    @Autowired
    public LicenseServiceImpl(MessageSource messages, LicenseRepository licenseRepository, ServiceConfig config) {
        this.messages = messages;
        this.licenseRepository = licenseRepository;
        this.config = config;
    }

    public License getLicense(String licenseId, String organizationId, Locale locale){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(String.format(messages.getMessage("license.search.error.message", null, locale),licenseId, organizationId));
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
}