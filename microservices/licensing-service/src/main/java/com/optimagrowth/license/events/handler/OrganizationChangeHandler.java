package com.optimagrowth.license.events.handler;


import com.optimagrowth.license.events.CustomChannels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.optimagrowth.license.events.model.OrganizationChangeModel;

// Moves the @EnableBindings out of Application.java and into OrganizationChangeHandler.
// This time instead of using the Sink class, we use CustomChannels as the parameter to pass.
@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    // The utility class doing all the metadata extraction work
    @StreamListener("inboundOrgChanges")
    // Inspects the action that the data undertakes and then reacts accordingly
    public void loggerSink(OrganizationChangeModel organization) {

        logger.debug("Received a message of type " + organization.getType());

        switch(organization.getAction()){
            case "GET":
                logger.debug("Received a GET event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case "SAVE":
                logger.debug("Received a SAVE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case "UPDATE":
                logger.debug("Received a UPDATE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case "DELETE":
                logger.debug("Received a DELETE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            default:
                logger.error("Received an UNKNOWN event from the organization service of type {}", organization.getType());
                break;
        }
    }


}