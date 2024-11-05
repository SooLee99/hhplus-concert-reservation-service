package org.example.hhplusconcertreservationservice.global;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi concertsApi() {
        return GroupedOpenApi.builder()
                .group("concerts")
                .pathsToMatch("/concerts/**")
                .addOpenApiCustomiser(openApi -> openApi
                        .info(new Info()
                                .title("Concerts API")
                                .description("Concert 관련 API 명세서")
                                .version("1.0")
                                .contact(new Contact()
                                        .name("HHPlus")
                                        .url("https://hhplus.com")
                                        .email("support@hhplus.com"))
                                .license(new License()
                                        .name("HHPlus License")
                                        .url("https://hhplus.com/license"))))
                .build();
    }

    @Bean
    public GroupedOpenApi reservationsApi() {
        return GroupedOpenApi.builder()
                .group("reservations")
                .pathsToMatch("/reservations/**")
                .addOpenApiCustomiser(openApi -> openApi
                        .info(new Info()
                                .title("Reservations API")
                                .description("Reservation 관련 API 명세서")
                                .version("1.0")
                                .contact(new Contact()
                                        .name("HHPlus")
                                        .url("https://hhplus.com")
                                        .email("support@hhplus.com"))
                                .license(new License()
                                        .name("HHPlus License")
                                        .url("https://hhplus.com/license"))))
                .build();
    }

    @Bean
    public GroupedOpenApi paymentsApi() {
        return GroupedOpenApi.builder()
                .group("payments")
                .pathsToMatch("/payments/**")
                .addOpenApiCustomiser(openApi -> openApi
                        .info(new Info()
                                .title("Payments API")
                                .description("Payment 관련 API 명세서")
                                .version("1.0")
                                .contact(new Contact()
                                        .name("HHPlus")
                                        .url("https://hhplus.com")
                                        .email("support@hhplus.com"))
                                .license(new License()
                                        .name("HHPlus License")
                                        .url("https://hhplus.com/license"))))
                .build();
    }

    @Bean
    public GroupedOpenApi seatsApi() {
        return GroupedOpenApi.builder()
                .group("seats")
                .pathsToMatch("/seats/**")
                .addOpenApiCustomiser(openApi -> openApi
                        .info(new Info()
                                .title("Seats API")
                                .description("Seats 관련 API 명세서")
                                .version("1.0")
                                .contact(new Contact()
                                        .name("HHPlus")
                                        .url("https://hhplus.com")
                                        .email("support@hhplus.com"))
                                .license(new License()
                                        .name("HHPlus License")
                                        .url("https://hhplus.com/license"))))
                .build();
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/users/**")
                .addOpenApiCustomiser(openApi -> openApi
                        .info(new Info()
                                .title("Users API")
                                .description("Users 관련 API 명세서")
                                .version("1.0")
                                .contact(new Contact()
                                        .name("HHPlus")
                                        .url("https://hhplus.com")
                                        .email("support@hhplus.com"))
                                .license(new License()
                                        .name("HHPlus License")
                                        .url("https://hhplus.com/license"))))
                .build();
    }
}
