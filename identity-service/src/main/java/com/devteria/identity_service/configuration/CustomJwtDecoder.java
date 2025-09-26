package com.devteria.identity_service.configuration;

import com.devteria.identity_service.dto.IntrospectRequest;
import com.devteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    private final AuthenticationService authenticationService;
    private NimbusJwtDecoder jwtDecoder;
    protected static final String SIGNER_KEY = "p7cHINXNIOg7JEYDrVOYKzMREMuZtAtuZzWsz00TyCX+CikSXSjoLImFBx6ZrsJ6";
    @Override
    public Jwt decode(String token) throws JwtException {
        try{
            var response =  authenticationService.introspect(IntrospectRequest.builder().token(token).build());
            if(!response.isValid()) throw new JwtException("Token invalid");
        }catch (JOSEException | ParseException e){
            throw new JwtException(e.getMessage());
        }
        if(Objects.isNull(jwtDecoder)){
            SecretKeySpec key = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");
            jwtDecoder =NimbusJwtDecoder.withSecretKey(key)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

        }
        return jwtDecoder.decode(token);
    }
}
