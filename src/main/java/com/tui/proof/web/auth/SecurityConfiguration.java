package com.tui.proof.web.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests(a ->
                a.mvcMatchers(HttpMethod.GET, "/orders").authenticated().anyRequest().permitAll()
        ).httpBasic();

        http.cors().and().csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                // -- Swagger UI v3 (OpenAPI)
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "**/h2-console/**");
    }

}
