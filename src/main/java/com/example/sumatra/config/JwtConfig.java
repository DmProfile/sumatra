package com.example.sumatra.config;

import com.example.sumatra.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${app.secret-key}")
    private String secretKey;


    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtFilter(secretKey));
        filterRegistrationBean.addUrlPatterns("/api/v1/users/*"); // Define the URL pattern for which the filter should be applied
        filterRegistrationBean.addUrlPatterns("/api/v1/users/*"); // Define the URL pattern for which the filter should be applied
        return filterRegistrationBean;
    }
}
