package com.myshop.api.config;

import com.myshop.security.audit.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class PersistenceConfig {
    @Bean
    AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }

}