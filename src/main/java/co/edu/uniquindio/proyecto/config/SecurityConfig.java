package co.edu.uniquindio.proyecto.config;


import co.edu.uniquindio.proyecto.seguridad.AutenticacionEntryPoint;
import co.edu.uniquindio.proyecto.seguridad.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JWTFilter jwtFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configura la seguridad HTTP para la aplicación
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/login/**","/api/usuarios/notificacion","/api/usuarios/Activar","/api/usuarios", "/api/imagenes").permitAll()
                        // Permitir SOLO GET en /api/moderador/** para CLIENTE y MODERADOR
                        .requestMatchers(HttpMethod.GET, "/api/moderador/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_MODERADOR")
                        .requestMatchers("/api/moderador/**").hasAuthority("ROLE_MODERADOR")  // Solo moderadores
                        .requestMatchers("/api/usuarios/**","/api/us","/api/reportes/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_MODERADOR") // Clientes y moderadores
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint( new AutenticacionEntryPoint() ))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Configura las políticas de CORS para permitir solicitudes desde el frontend
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "https://reporte-ciudadano-front.web.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Permite codificar y verificar contraseñas utilizando BCrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        // Proporciona un AuthenticationManager para la autenticación de usuarios
        return configuration.getAuthenticationManager();
    }
}

/*.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/login/**","/api/usuarios/notificacion","/api/usuarios/Activar","/api/usuarios", "/api/imagenes").permitAll()
// Permitir SOLO GET en /api/moderador/** para CLIENTE y MODERADOR
                        .requestMatchers(HttpMethod.GET, "/api/moderador/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_MODERADOR")
                        .requestMatchers("/api/moderador/**").hasAuthority("ROLE_MODERADOR")  // Solo moderadores
                        .requestMatchers("/api/usuarios/**","/api/us","/api/reportes/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_MODERADOR") // Clientes y moderadores
                        .anyRequest().authenticated()*/