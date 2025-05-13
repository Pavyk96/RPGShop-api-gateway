package payk96.api_gateway.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class GatewayLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(GatewayLoggingAspect.class);

    @Around("execution(* org.springframework.cloud.gateway.filter.GlobalFilter.filter(..))")
    public Mono<Void> logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        ServerWebExchange exchange = (ServerWebExchange) joinPoint.getArgs()[0];
        long startTime = System.currentTimeMillis();

        log.info(
                "Incoming request: {} {}, Headers: {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath(),
                exchange.getRequest().getHeaders()
        );

        return ((Mono<Void>) joinPoint.proceed())
                .doOnSuccess(v -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info(
                            "Request {} {} completed in {} ms (Status: {})",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath(),
                            duration,
                            exchange.getResponse().getStatusCode()
                    );
                })
                .doOnError(error -> {
                    log.error(
                            "Error in request {} {}: {}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getPath(),
                            error.getMessage()
                    );
                });
    }
}
