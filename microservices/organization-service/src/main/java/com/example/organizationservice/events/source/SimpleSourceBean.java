package com.example.organizationservice.events.source;

import com.example.organizationservice.events.model.OrganizationChangeModel;
import com.example.organizationservice.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimpleSourceBean {
    private Source source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    // Injects a Source interface implementation for use by the service
    @Autowired
    public SimpleSourceBean(Source source){
        this.source = source;
    }

    public void publishOrganizationChange(String action, String organizationId){
        logger.debug("Sending Kafka message {} for Organization Id: {}", action, organizationId);
        OrganizationChangeModel change =  new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action,
                organizationId,
                // Publishes a Java POJO message
                UserContext.getCorrelationId());

        // Sends the message from a channel defined in the Source class
        source.output().send(MessageBuilder.withPayload(change).build());
    }
}