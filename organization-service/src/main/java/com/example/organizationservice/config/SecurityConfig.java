package com.example.organizationservice.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration // The class must be marked with @Configuration
@EnableWebSecurity // Applies the configuration to the global WebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true) // Enables @RoleAllowed annotation
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    // Registers the Keycloak authentication provider
    // All access rules are defined inside the configure() method
    @Override
    protected void configure(HttpSecurity http)throws Exception {
        super.configure(http);
        // we will restrict all access to any URL in the organization service to authenticated users only
        http.authorizeRequests()
                .anyRequest().authenticated();
        http.csrf().disable();
    }

    // Defines the session authentication strategy
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    // Defines the session authentication strategy
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    @Primary
    public KeycloakConfigResolver KeycloakConfigResolver(KeycloakSpringBootProperties properties) {
        return new ExampleKeycloakSpringBootConfigResolver(properties);
    }
}