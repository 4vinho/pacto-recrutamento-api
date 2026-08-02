package br.com.pacto.recrutamento.web.config;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestLoggingFilterTest {
    private final RequestLoggingFilter filter = new RequestLoggingFilter();

    @Test
    void deveReutilizarIdentificadorValidoRecebido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vagas");
        request.addHeader(RequestLoggingFilter.REQUEST_ID_HEADER, "frontend-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("frontend-123", response.getHeader(RequestLoggingFilter.REQUEST_ID_HEADER));
        assertNull(MDC.get(RequestLoggingFilter.REQUEST_ID_MDC_KEY));
    }

    @Test
    void deveGerarIdentificadorQuandoRecebidoForInseguro() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vagas");
        request.addHeader(RequestLoggingFilter.REQUEST_ID_HEADER, "id com quebra\nforjada");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String requestId = response.getHeader(RequestLoggingFilter.REQUEST_ID_HEADER);
        assertFalse(requestId.contains("\n"));
        assertFalse(requestId.contains(" "));
        assertNull(MDC.get(RequestLoggingFilter.REQUEST_ID_MDC_KEY));
    }
}
