package com.corebank.coreb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ActivityInterceptor activityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(activityInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/staff/login", "/api/staff"); // 👈 exclude these
    }

}
