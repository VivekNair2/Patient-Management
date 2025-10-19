package com.pm.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {


          private final WebClient webClient;
            public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,@Value("${auth.service.url}") String authServiceUrl) {
                super(Object.class);
                this.webClient = webClientBuilder.baseUrl("http://auth-service").build();
            }
          @Override
          public GatewayFilter apply(Object config){

          }
}
