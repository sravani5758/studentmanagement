package com.example.studentmanagement.config;

import com.example.studentmanagement.security.AuthEntryPointJwt;
import com.example.studentmanagement.security.CustomUserDetailsService;
import com.example.studentmanagement.security.JwtAuthenticationFilter;
import com.example.studentmanagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/error"
                        ).permitAll()

                        // Student endpoints - specific patterns
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/students/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/students/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("ADMIN")

                        // Instructor endpoints
                        .requestMatchers(HttpMethod.GET, "/api/instructors/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/instructors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/instructors/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/instructors/**").hasRole("ADMIN")

                        // Course endpoints
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll() // or hasAnyRole(...)
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("ADMIN")

                        // Assignment endpoints
                        .requestMatchers(HttpMethod.GET, "/api/assignments/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/assignments/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/assignments/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/assignments/**").hasRole("ADMIN")

                        // Submission endpoints
                        .requestMatchers(HttpMethod.GET, "/api/submissions/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/submissions/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/submissions/**").hasAnyRole("ADMIN", "INSTRUCTOR")

                        // Enrollment endpoints
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/enrollments/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/enrollments/**").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/enrollments/**").hasRole("ADMIN")

                        // Any other request
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}