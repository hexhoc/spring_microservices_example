package com.optimagrowth.license;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.util.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
// Spring Boot Actuator offers a @RefreshScope annotation that allows a development team to access a /refresh endpoint that will force the
// Spring Boot application to reread its application configuration.
@RefreshScope
@EnableDiscoveryClient
//Feign – a declarative HTTP client developed by Netflix. Feign aims at simplifying HTTP API clients. Simply put, the
//developer needs only to declare and annotate an interface while the actual implementation is provisioned at runtime
@EnableFeignClients
// Tells the service to the use the channels defined in the Sink interface to listen for incoming messages
// This Sink interface channel is called input and is used to listen for incoming messages.
public class LicenseServiceApplication {
//TODO Add tests

    private final ServiceConfig serviceConfig;

    public static final Logger logger = LoggerFactory.getLogger(LicenseServiceApplication.class);

    @Autowired
    public LicenseServiceApplication(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(LicenseServiceApplication.class, args);
    }


    // Bean for work with redis
    //  Spring uses the Jedis open source project to communicate with a Redis server. To communicate with a specific
    //Redis instance, we’ll expose a JedisConnectionFactory class as a Spring bean.
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        String hostname = serviceConfig.getRedisServer();
        int port = Integer.parseInt(serviceConfig.getRedisPort());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostname, port);
        //redisStandaloneConfiguration.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    // Bean for work with redis
    // Once we have a connection to Redis, we’ll use that connection to create a Spring
    //RedisTemplate object. The Spring Data repository classes that we’ll implement
    //shortly use the RedisTemplate object to execute the queries and saves of organization service data to our Redis service.
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    // For internationalization and adapt our application to different language we create local resolver and
    // resource bundle message source
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        //set US as the default local
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // Doesn't throw an error if a message isn't found, instead it returns the message code
        // When a message is not found, this option returns the message code 'license.creates.message' instead of an error like this one:
        // "No message found under code 'license.creates.message' for locale 'es'
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        // Sets the base name of the languages properties files
        // we will have next messages files: messages_en.properties, messages_ru.properties and messages.properties
        messageSource.setBasename("messages");
        return messageSource;
    }

    // To use a Load Balancer–aware RestTemplate class, we need to
    // define a RestTemplate bean with a Spring Cloud @LoadBalanced annotation
    // This is one of the more common mechanisms for interacting with the Load Balancer via Spring
    @SuppressWarnings("unchecked")
    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        RestTemplate template = new RestTemplate();
        // we’ll use the RestTemplate created with the UserContextInterceptor attached to it.
        List interceptors = template.getInterceptors();
        if (interceptors==null){
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        }else{
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }
        return template;
    }

}
