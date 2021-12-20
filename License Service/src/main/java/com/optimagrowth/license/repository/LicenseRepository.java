package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends JpaRepository<License, String> {
    List<License> findByOrganizationId(String organizationId);
    License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);
}
