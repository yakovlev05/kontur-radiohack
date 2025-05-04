package ru.yakovlev05.hackaton.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${security.basic.username}")
    private String username;

    @Value("${security.basic.password}")
    private String password;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(r -> r
                        .requestMatchers("/api/v1/questions/**").authenticated()
                        .requestMatchers("/api/v1/statistics/starts-games").authenticated()
                        .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username(username)
                .password("{noop}" + password)
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

}
