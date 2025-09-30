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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  protected static final String SIGNER_KEY =
      "p7cHINXNIOg7JEYDrVOYKzMREMuZtAtuZzWsz00TyCX+CikSXSjoLImFBx6ZrsJ6";

  @Autowired private CustomJwtDecoder customJwtDecoder;
  @Autowired private AuthenticationService authenticationService;
  @Autowired private OAuth2AuthorizedClientService authorizedClientService;

  @Order(2)
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(
        request ->
            request
                .requestMatchers(HttpMethod.POST)
                .permitAll()
                .requestMatchers(HttpMethod.GET)
                .permitAll()
                .requestMatchers(HttpMethod.DELETE)
                .permitAll()
                .requestMatchers(HttpMethod.PUT)
                .permitAll()
                .anyRequest()
                .authenticated());
    httpSecurity.oauth2ResourceServer(
        oauth2 ->
            oauth2
                .jwt(
                    jwtConfigurer ->
                        jwtConfigurer
                            .decoder(customJwtDecoder)
                            .jwtAuthenticationConverter(authenticationConverter()))
                .authenticationEntryPoint(new JWTAuthenticationEntryPoint()));
    httpSecurity.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());
    return httpSecurity.build();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/oauth2/**", "/login/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .oauth2Login(
            oauth2 ->
                oauth2
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                    .successHandler(
                        (request, response, authentication) -> {
                          OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                          String email = oAuth2User.getAttribute("email");
                          String name = oAuth2User.getAttribute("name");
                          User user = authenticationService.findOrCreateUser(email, name);

                          OAuth2AuthenticationToken oauthToken =
                              (OAuth2AuthenticationToken) authentication;

                          // Lấy client đã được Spring lưu
                          OAuth2AuthorizedClient client =
                              authorizedClientService.loadAuthorizedClient(
                                  oauthToken.getAuthorizedClientRegistrationId(),
                                  oauthToken.getName());

                          // Token Google gốc
                          String googleAccessToken = client.getAccessToken().getTokenValue();

                          // Token JWT của app
                          String appToken = authenticationService.generateToken(user);

                          // Trả JSON đẹp về client
                          response.setContentType("application/json");
                          response.setCharacterEncoding("UTF-8");
                          response
                              .getWriter()
                              .write(
                                  "{\n"
                                      + "  \"appToken\": \""
                                      + appToken
                                      + "\",\n"
                                      + "  \"googleAccessToken\": \""
                                      + googleAccessToken
                                      + "\"\n"
                                      + "}");
                          //                          response.sendRedirect(
                          //                              "http://localhost:5173/" + appToken + "/"
                          // + googleAccessToken);
                        }));
    return http.build();
  }

  private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
    return new DefaultOAuth2UserService();
  }

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOriginPattern("*");
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  @Bean
  JwtAuthenticationConverter authenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(10);
  }
}
