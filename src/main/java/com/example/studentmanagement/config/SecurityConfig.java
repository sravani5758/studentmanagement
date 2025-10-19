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
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/students/{id}").hasAnyRole("STUDENT", "ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/students/{id}").hasAnyRole("STUDENT", "ADMIN") // Students can update their own profile
                        .requestMatchers(HttpMethod.DELETE, "/api/students/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/students").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/students").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/students/{studentId}/recalculate-gpa").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/students/search").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/{id}/soft-delete").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/instructors/{id}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/instructors/{id}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/instructors").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/instructors").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/instructors/department/{department}").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/instructors/{id}/soft-delete").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/courses/{id}").authenticated() // All authenticated users can view courses
                        .requestMatchers(HttpMethod.PUT, "/api/courses/{id}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/courses").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/courses/{courseId}/availability").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/courses/instructor/{instructorId}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses/active").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/{id}/soft-delete").hasAnyRole("ADMIN", "INSTRUCTOR")


                        .requestMatchers(HttpMethod.GET, "/api/assignments/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/assignments/{id}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/assignments/{id}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/assignments").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/assignments").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/assignments/course/{courseId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/assignments/active").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/assignments/{id}/soft-delete").hasAnyRole("INSTRUCTOR", "ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/submissions").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/submissions").hasAnyRole("STUDENT","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/submissions/{submissionId}/grade").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/submissions/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/submissions/student/{studentId}").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/submissions/course/{courseId}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/submissions/assignment/{assignmentId}").hasAnyRole("INSTRUCTOR", "ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/enrollments").hasAnyRole("ADMIN", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/enrollments").hasAnyRole("STUDENT","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/enrollments/{enrollmentId}/calculate-final-grade").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/enrollments/{enrollmentId}/grade").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/student/{studentId}").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/course/{courseId}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/enrollments/{id}/soft-delete").hasAnyRole("ADMIN", "INSTRUCTOR")





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