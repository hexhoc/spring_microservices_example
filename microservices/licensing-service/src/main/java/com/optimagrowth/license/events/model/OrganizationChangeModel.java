package com.optimagrowth.license.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OrganizationChangeModel {
    private String type;
    // This is the action that triggered the event. We’ve included the action
    // element in the message to give the message consumer more context on how it  should process an event
    private String action;
    private String organizationId;
    private String correlationId;

    public OrganizationChangeModel(){
        super();
    }

    public  OrganizationChangeModel(String type, String action, String organizationId, String correlationId) {
        super();
        this.type   = type;
        this.action = action;
        this.organizationId = organizationId;
        this.correlationId = correlationId;
    }
}