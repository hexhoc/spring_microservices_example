package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;

import java.util.List;
import java.util.Locale;

public interface LicenseService {

    License getLicense(String licenseId, String organizationId, String clientType, Locale locale);

    License createLicense(License license);

    License updateLicense(License license);

    String deleteLicense(String licenseId, Locale locale);

    List<License> getLicensesByOrganization(String organizationId);

    List<License> buildFallbackLicenseList(String organizationId, Throwable t);
}
