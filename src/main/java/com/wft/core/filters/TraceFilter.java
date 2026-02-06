package com.wft.core.filters;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceFilter extends OncePerRequestFilter {

    private static final String X_TRACE_ID = "X-Trace-Id";
    private static final String X_SPAN_ID = "X-Span-Id";
    private final Tracer tracer;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain chain
    ) throws ServletException, IOException {

        Span currentSpan = tracer.currentSpan();
        try {
            if (Objects.nonNull(currentSpan)) {
                String traceId = currentSpan.context().traceId();
                String spanId = currentSpan.context().spanId();

                response.setHeader(X_TRACE_ID, traceId);
                response.setHeader(X_SPAN_ID, spanId);

                MDC.put(X_TRACE_ID, traceId);
                MDC.put(X_SPAN_ID, spanId);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(X_TRACE_ID);
            MDC.remove(X_SPAN_ID);
        }
    }
}
