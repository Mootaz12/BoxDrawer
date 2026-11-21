package com.boxdrawer.gateway;

import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/** Browsers get the HTML error page for any error: downstream, security or gateway. API clients keep JSON. */
@Component
public class HtmlErrorPageFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!wantsHtml(exchange)) {
            return chain.filter(exchange);
        }
        ServerHttpResponse original = exchange.getResponse();
        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(original) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (!isError(getStatusCode())) {
                    return super.writeWith(body);
                }
                // Release the downstream body, then send the page instead
                return Flux.from(body).doOnNext(DataBufferUtils::release)
                        .then(writePage(getDelegate(), getStatusCode().value()));
            }

            @Override
            public Mono<Void> setComplete() {
                return isError(getStatusCode()) ? writePage(getDelegate(), getStatusCode().value()) : super.setComplete();
            }
        };
        return chain.filter(exchange.mutate().response(response).build())
                .onErrorResume(ex -> original.isCommitted() ? Mono.error(ex) : writePage(original, statusOf(ex)));
    }

    private static boolean wantsHtml(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getAccept().stream()
                .anyMatch(m -> "text".equals(m.getType()) && "html".equals(m.getSubtype()));
    }

    private static boolean isError(HttpStatusCode status) {
        return status != null && status.value() >= 400;
    }

    private static int statusOf(Throwable ex) {
        return ex instanceof ResponseStatusException rse ? rse.getStatusCode().value() : 500;
    }

    private static Mono<Void> writePage(ServerHttpResponse response, int status) {
        byte[] bytes = ErrorPage.render(status).getBytes(StandardCharsets.UTF_8);
        response.setStatusCode(HttpStatusCode.valueOf(status));
        HttpHeaders headers = response.getHeaders();
        headers.remove(HttpHeaders.TRANSFER_ENCODING);
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentLength(bytes.length);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
