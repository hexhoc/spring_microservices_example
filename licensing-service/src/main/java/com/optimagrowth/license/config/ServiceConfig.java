package com.optimagrowth.license.config;

import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

//Trying to find all properties from application.properties with prefix "example"
@Component
@Getter @Setter
public class ServiceConfig{

    @Value("${example.property}")
    private String property;

    @Value("${example.gateway}")
    private String gateway;

    @Value("${redis.server}")
    private String redisServer="";

    @Value("${redis.port}")
    private String redisPort="";

}