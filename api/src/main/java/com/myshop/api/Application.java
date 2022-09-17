package com.myshop.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories("com.myshop.repositories")
@EntityScan("com.myshop.repositories")

@SpringBootApplication(
        exclude = {ErrorWebFluxAutoConfiguration.class, ValidationAutoConfiguration.class},
        scanBasePackages = {"com.myshop.common", "com.myshop.repositories", "com.myshop.api", "com.myshop.security.jwt" })
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        application.run(args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));   // It will set UTC timezone
        System.out.println("Spring boot application running in UTC timezone :" + new Date());   // It will print UTC timezone
    }

    @Bean
    public RouterFunction<ServerResponse> swaggerUI() {
        return RouterFunctions.route(RequestPredicates.GET("/swagger"),
                request -> ServerResponse.permanentRedirect(URI.create(request.path() + "/index.html?url=/api-docs")).build()).and(RouterFunctions
                .resources("/swagger/**", new ClassPathResource("swagger-ui/")));
    }

}
