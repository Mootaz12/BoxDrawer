package com.boxdrawer.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Copies the username from the JWT into an X-User header so downstream services know who calls. */
@Component
public class UserHeaderFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(
    ServerWebExchange exchange,
    GatewayFilterChain chain
  ) {
    return ReactiveSecurityContextHolder.getContext()
      .map(ctx -> ctx.getAuthentication())
      .filter(auth -> auth instanceof JwtAuthenticationToken)
      .map(auth ->
        ((JwtAuthenticationToken) auth)
          .getToken()
          .getClaimAsString("preferred_username")
      )
      .map(user ->
        exchange.mutate().request(withUser(exchange.getRequest(), user)).build()
      )
      .defaultIfEmpty(exchange)
      .flatMap(chain::filter);
  }

  // Incoming headers are read-only here, so expose a writable copy
  private static ServerHttpRequest withUser(
    ServerHttpRequest request,
    String user
  ) {
    HttpHeaders headers = new HttpHeaders();
    headers.putAll(request.getHeaders());
    headers.set("X-User", user);
    return new ServerHttpRequestDecorator(request) {
      @Override
      public HttpHeaders getHeaders() {
        return headers;
      }
    };
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
