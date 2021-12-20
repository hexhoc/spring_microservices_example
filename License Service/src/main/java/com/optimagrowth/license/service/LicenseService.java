package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;

import java.util.Locale;
import java.util.UUID;

public interface LicenseService {

    License getLicense(String licenseId, String organizationId, Locale locale);

    License createLicense(License license);

    License updateLicense(License license);

    String deleteLicense(String licenseId, Locale locale);

}
