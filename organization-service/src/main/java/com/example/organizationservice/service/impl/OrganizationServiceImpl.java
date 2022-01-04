package com.example.organizationservice.service.impl;

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

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository repository, SimpleSourceBean simpleSourceBean) {
        this.repository = repository;
        this.simpleSourceBean = simpleSourceBean;
    }

    public Organization findById(String organizationId) {
        Optional<Organization> opt = repository.findById(organizationId);
        simpleSourceBean.publishOrganizationChange(ActionEnum.GET.toString(), organizationId);
        return opt.orElse(null);
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