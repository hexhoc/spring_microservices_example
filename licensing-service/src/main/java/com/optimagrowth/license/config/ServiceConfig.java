package com.optimagrowth.license.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
//Trying to find all properties from application.properties with prefix "example"
@ConfigurationProperties(prefix = "example")
@Getter @Setter
public class ServiceConfig{

    private String property;

}