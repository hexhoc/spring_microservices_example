package com.optimagrowth.license.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

// if we want to define more than one channel for our application, or we want to customize the names of our
//channels, we can define our own interface and expose as many input and output channels as our application needs
public interface CustomChannels {

    @Input("inboundOrgChanges")
    SubscribableChannel orgs();

}