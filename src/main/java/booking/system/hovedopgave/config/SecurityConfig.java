package booking.system.hovedopgave.config;

import booking.system.hovedopgave.security.AdminDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminDetailsService adminDetailsService;

    @Autowired
    public SecurityConfig(AdminDetailsService adminDetailsService) {
        this.adminDetailsService = adminDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (because we're using a stateless frontend or REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS and load configuration from our custom CorsConfigurationSource
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Define which endpoints are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        // Public GET endpoints
                        .requestMatchers(HttpMethod.GET, "/api/offered-services").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/timeslots/service/{serviceId}").permitAll()
                        // Public POST endpoint for making bookings
                        .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()
                        // Everything else must be authenticated
                        .anyRequest().authenticated()
                )

                // Configure form-based login (used by the frontend to log in via POST to /login)
                .formLogin(form -> form
                        .loginProcessingUrl("/login") // This is the endpoint the frontend posts to
                        .successHandler((request, response, authentication) -> response.setStatus(200)) // Return 200 on success
                        .failureHandler((request, response, exception) -> response.setStatus(401))     // Return 401 on failure
                        .permitAll()
                )

                // Return 401 instead of redirect when unauthenticated access is attempted (important for SPAs)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                        })
                )

                // Configure logout behavior
                .logout(logout -> logout
                        .logoutUrl("/logout") // The endpoint to trigger logout
                        .invalidateHttpSession(true) // Invalidate the HTTP session
                        .deleteCookies("JSESSIONID") // Remove the session cookie
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200); // Tell frontend logout succeeded
                        })
                )

                // Use our custom user details service (loads Admin users)
                .userDetailsService(adminDetailsService);

        return http.build(); // Build and return the security filter chain
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Define which origins, methods, and headers are allowed for cross-origin requests
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // Allow frontend to communicate with backend
        configuration.addAllowedMethod("*");                     // Allow all HTTP methods (GET, POST, etc.)
        configuration.setAllowCredentials(true);                 // Allow cookies to be sent
        configuration.addAllowedHeader("*");                     // Allow all headers

        // Register this configuration for all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use bcrypt to hash passwords (required by Spring Security)
        return new BCryptPasswordEncoder();
    }
}
