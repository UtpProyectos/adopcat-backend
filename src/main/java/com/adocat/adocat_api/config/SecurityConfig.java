package com.adocat.adocat_api.config;

import com.adocat.adocat_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${vite.url}")
    private String viteUrl;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // ✅ ACTIVA CORS
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/cats", "/api/cats/{id}").permitAll() //

                        // 📦 Catálogo público
                        .requestMatchers("/api/products", "/api/products/{id}").permitAll()

                        // ✅ EXCEPCIÓN para prueba de correo
                        .requestMatchers("/api/orders/test-email").permitAll()

                        // 🛠️ Gestión de productos (admin)
                        .requestMatchers("/api/products/**").hasRole("ADMIN")
                        .requestMatchers("/api/product-categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/product-suppliers/**").hasRole("ADMIN")

                        // 🛒 Checkout solo adoptante
                                .requestMatchers("/api/orders").permitAll()
// O si necesitas autenticación:
                                .requestMatchers("/api/orders").hasRole("ADOPTANTE")

                        // 🧾 Order items (solo admin si deseas)
                        .requestMatchers("/api/order-items/**").hasRole("ADMIN")

                        // 🧍 Acceso general
                        .requestMatchers("/api/users/me").authenticated() // común
                        .requestMatchers("/api/users/**").hasAnyRole("ADOPTANTE","ADMIN","RESCATISTA")
                        .requestMatchers("/api/cats/**").hasAnyRole("ADOPTANTE","ADMIN","RESCATISTA")
                        .requestMatchers("/api/adoptions/**").hasAnyRole("ADOPTANTE","ADMIN","RESCATISTA")

                        // 🔐 Todo lo demás necesita login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(viteUrl)
                        //.allowedOrigins(viteUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}
