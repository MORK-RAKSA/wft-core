package com.wft.core.config;

import com.wft.core.services.resolvers.SnakeModelAttributeResolver;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(@Nonnull List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SnakeModelAttributeResolver());
    }
}
