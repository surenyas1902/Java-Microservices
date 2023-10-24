package com.learning.microservices.UsersMicroservice.security;

import com.learning.microservices.UsersMicroservice.service.api.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    @Value("${gateway.ip}")
    private String ipAddress;

    @Autowired
    private UsersService usersService;

    @Autowired
    private Environment environment;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(usersService).passwordEncoder(passwordEncoder);
        AuthenticationManager authManager = builder.build();
        System.out.println("Inside Configuration");
        http.csrf((csrf) -> csrf.disable());

        AuthenticationFilter authFilter = new AuthenticationFilter(authManager, usersService, environment);
        authFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
        http.authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, "/users")
                        .access(new WebExpressionAuthorizationManager("hasIpAddress('"+ipAddress+"')")))
                .addFilter(authFilter)
                .authenticationManager(authManager)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));
        return http.build();
    }
}
