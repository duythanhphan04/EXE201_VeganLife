package com.devteria.identity_service.configuration;

import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    protected static final String SIGNER_KEY =
            "p7cHINXNIOg7JEYDrVOYKzMREMuZtAtuZzWsz00TyCX+CikSXSjoLImFBx6ZrsJ6";

    @Autowired private CustomJwtDecoder customJwtDecoder;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private OAuth2AuthorizedClientService authorizedClientService;

    /**
     * ✅ FilterChain #2: Dành cho API, WebSocket và các endpoint thông thường
     */
    @Order(2)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(request -> request
                        // 🔓 Cho phép WebSocket, SockJS, STOMP connect
                        .requestMatchers(
                                "/chat-websocket/**", "/ws/**", "/topic/**", "/queue/**", "/app/**"
                        ).permitAll()

                        // 🔓 Cho phép các request REST cơ bản (nếu bạn muốn)
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .requestMatchers(HttpMethod.POST).permitAll()
                        .requestMatchers(HttpMethod.PUT).permitAll()
                        .requestMatchers(HttpMethod.DELETE).permitAll()

                        // 🔒 Các endpoint còn lại cần JWT
                        .anyRequest().authenticated()
                )

                // ⚙️ Cấu hình OAuth2 Resource Server (JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(authenticationConverter())
                        )
                        .authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                )

                // ❌ Tắt CSRF để STOMP và REST hoạt động bình thường
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ Bật CORS global (dùng bean bên dưới)
                .cors(Customizer.withDefaults());

        return http.build();
    }

    /**
     * ✅ FilterChain #1: Dành cho OAuth2 Login (Google, v.v.)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");
                            String name = oAuth2User.getAttribute("name");

                            // Tạo hoặc lấy user từ DB
                            User user = authenticationService.findOrCreateUser(email, name);

                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

                            // Lấy access token gốc của Google
                            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                                    oauthToken.getAuthorizedClientRegistrationId(),
                                    oauthToken.getName());

                            String googleAccessToken = client.getAccessToken().getTokenValue();

                            // Sinh JWT app token
                            String appToken = authenticationService.generateToken(user);

                            // Redirect lại frontend
                            response.sendRedirect(
                                    "http://localhost:5173/oauth2/success?appToken=" + appToken
                                            + "&googleToken=" + googleAccessToken
                            );
                        })
                );

        return http.build();
    }

    /**
     * ✅ Cấu hình user service cho OAuth2 (Google)
     */
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new DefaultOAuth2UserService();
    }

    /**
     * ✅ CORS Config Global (cho phép frontend localhost:5173)
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * ✅ JWT converter: để Spring Security hiểu các role trong token
     */
    @Bean
    JwtAuthenticationConverter authenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    /**
     * ✅ Password encoder cho app
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(10);
    }
}