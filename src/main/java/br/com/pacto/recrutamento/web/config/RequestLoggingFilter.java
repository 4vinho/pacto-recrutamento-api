package br.com.pacto.recrutamento.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    static final String REQUEST_ID_HEADER = "X-Request-ID";
    static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final int REQUEST_ID_MAX_LENGTH = 100;
    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("[A-Za-z0-9._:-]+");
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = obterRequestId(request.getHeader(REQUEST_ID_HEADER));
        long inicio = System.nanoTime();
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duracaoMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio);
            LOGGER.info("Requisicao concluida: metodo={}, rota={}, status={}, duracaoMs={}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duracaoMs);
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }

    private String obterRequestId(String recebido) {
        if (recebido != null && recebido.length() <= REQUEST_ID_MAX_LENGTH
                && REQUEST_ID_PATTERN.matcher(recebido).matches()) {
            return recebido;
        }
        return UUID.randomUUID().toString();
    }
}
