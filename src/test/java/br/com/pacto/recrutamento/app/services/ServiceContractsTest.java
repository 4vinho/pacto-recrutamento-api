package br.com.pacto.recrutamento.app.services;

import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceContractsTest {

    private final List<Class<?>> services = Arrays.asList(
            UsuarioService.class,
            CandidatoService.class,
            CurriculoService.class,
            VagaService.class,
            CandidaturaService.class,
            TemplateVagaService.class,
            NotificacaoService.class
    );

    @Test
    void contratosDosServicesSaoInterfaces() {
        assertThat(services).allMatch(Class::isInterface);
    }

    @Test
    void contratosNaoExpoemEntidadesJpa() {
        List<Method> methods = services.stream()
                .flatMap(service -> Arrays.stream(service.getDeclaredMethods()))
                .collect(Collectors.toList());

        assertThat(methods)
                .noneMatch(this::exposesJpaEntity);
    }

    private boolean exposesJpaEntity(Method method) {
        return isJpaEntity(method.getReturnType())
                || Arrays.stream(method.getParameterTypes()).anyMatch(this::isJpaEntity);
    }

    private boolean isJpaEntity(Class<?> type) {
        return type.isAnnotationPresent(Entity.class);
    }
}
