package com.learning.microservices.APIGateway.Filter;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${token.secret}")
    private String tokenSecret;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization Header", HttpStatus.UNAUTHORIZED);
            }
            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authHeader.replace("Bearer ", "");
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT Token is not valid", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {

    }

    public Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwtToken) {
        boolean isValid = true;

        byte[] secretKeys = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey signingKey = new SecretKeySpec(secretKeys, SignatureAlgorithm.HS512.getJcaName());

        String Subject = null;
        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(signingKey)
                .build();
        try {
            Jwt<Header, Claims> parsedToken = (Jwt<Header, Claims>) jwtParser.parse(jwtToken);
            Subject = parsedToken.getBody().getSubject();
        } catch (Exception ex) {
            isValid = false;
        }

        if (Subject == null || Subject.isEmpty()) {
            isValid = false;
        }
        return isValid;
    }
}
