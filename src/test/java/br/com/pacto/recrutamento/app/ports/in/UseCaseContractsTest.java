package br.com.pacto.recrutamento.app.ports.in;

import br.com.pacto.recrutamento.app.ports.in.candidato.CandidatoUseCase;
import br.com.pacto.recrutamento.app.ports.in.candidatura.CandidaturaUseCase;
import br.com.pacto.recrutamento.app.ports.in.curriculo.CurriculoUseCase;
import br.com.pacto.recrutamento.app.ports.in.notificacao.NotificacaoUseCase;
import br.com.pacto.recrutamento.app.ports.in.templatevaga.TemplateVagaUseCase;
import br.com.pacto.recrutamento.app.ports.in.usuario.UsuarioUseCase;
import br.com.pacto.recrutamento.app.ports.in.vaga.VagaUseCase;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class UseCaseContractsTest {

    private final List<Class<?>> services = Arrays.asList(
            UsuarioUseCase.class,
            CandidatoUseCase.class,
            CurriculoUseCase.class,
            VagaUseCase.class,
            CandidaturaUseCase.class,
            TemplateVagaUseCase.class,
            NotificacaoUseCase.class
    );

    @Test
    void contratosDosCasosDeUsoSaoInterfaces() {
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
