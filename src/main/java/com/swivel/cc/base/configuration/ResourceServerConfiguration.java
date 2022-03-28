package com.swivel.cc.base.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Slf4j
@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String LIST_BRAND_BASIC_INFO = "/api/v1/brands/basic/**";
    private static final String LIST_CATEGORY_BASIC_INFO = "/api/v1/categories/basic/**";

    private final String resourceId;

    public ResourceServerConfiguration(@Value("${security.oauth2.resource.id}") String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, LIST_BRAND_BASIC_INFO).permitAll()
                .antMatchers(HttpMethod.GET, LIST_CATEGORY_BASIC_INFO).permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceId);
    }
}