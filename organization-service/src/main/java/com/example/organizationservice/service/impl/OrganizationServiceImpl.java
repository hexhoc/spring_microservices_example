package com.example.organizationservice.service.impl;

import brave.ScopedSpan;
import brave.Tracer;
import com.example.organizationservice.events.ActionEnum;
import com.example.organizationservice.events.source.SimpleSourceBean;
import com.example.organizationservice.model.Organization;
import com.example.organizationservice.repository.OrganizationRepository;
import com.example.organizationservice.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepository repository;
    private final SimpleSourceBean simpleSourceBean;
    private final Tracer tracer;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository repository,
                                   SimpleSourceBean simpleSourceBean,
                                   Tracer tracer) {
        this.repository = repository;
        this.simpleSourceBean = simpleSourceBean;
        this.tracer = tracer;
    }

    public Organization findById(String organizationId) {
        Optional<Organization> opt = null;
        ScopedSpan newSpan = tracer.startScopedSpan("getOrgDBCall");
        try {
            opt = repository.findById(organizationId);
            simpleSourceBean.publishOrganizationChange(ActionEnum.GET.toString(), organizationId);
            if (!opt.isPresent()) {
                String message = String.format("Unable to find an organization with the Organization id %s", organizationId);
                logger.error(message);
                throw new IllegalArgumentException(message);
            }
            logger.debug("Retrieving Organization Info: " + opt.get().toString());
        }finally {
            newSpan.tag("peer.service", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }
        return opt.get();
    }

    public Organization create(Organization organization){
        organization.setId( UUID.randomUUID().toString());
        organization = repository.save(organization);
        simpleSourceBean.publishOrganizationChange(ActionEnum.SAVE.toString(), organization.getId());
        return organization;

    }

    public void update(Organization organization){
        repository.save(organization);
        simpleSourceBean.publishOrganizationChange(ActionEnum.UPDATE.toString(), organization.getId());
    }

    public void delete(Organization organization){
        repository.deleteById(organization.getId());
        simpleSourceBean.publishOrganizationChange(ActionEnum.DELETE.toString(), organization.getId());
    }
}