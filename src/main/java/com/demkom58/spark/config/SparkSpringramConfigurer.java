package com.demkom58.spark.config;

import com.demkom58.springram.controller.config.PathMatchingConfigurer;
import com.demkom58.springram.controller.config.SpringramConfigurer;
import com.demkom58.springram.controller.user.SpringramUserDetailsService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkSpringramConfigurer implements SpringramConfigurer {
    private final SpringramUserDetailsService userDetailsService;

    public SparkSpringramConfigurer(SpringramUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configurePathMatcher(PathMatchingConfigurer configurer) {
        configurer.setUserDetailsService(userDetailsService);
    }
}
