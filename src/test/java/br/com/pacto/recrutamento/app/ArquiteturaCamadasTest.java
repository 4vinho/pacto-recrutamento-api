package br.com.pacto.recrutamento.app;

import br.com.pacto.recrutamento.app.serviceImpl.CandidatoServiceImpl;
import br.com.pacto.recrutamento.app.serviceImpl.CandidaturaServiceImpl;
import br.com.pacto.recrutamento.app.serviceImpl.CurriculoServiceImpl;
import br.com.pacto.recrutamento.app.serviceImpl.NotificacaoServiceImpl;
import br.com.pacto.recrutamento.app.serviceImpl.TemplateVagaServiceImpl;
import br.com.pacto.recrutamento.app.serviceImpl.UsuarioServiceImpl;
import br.com.pacto.recrutamento.app.serviceImpl.VagaServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ArquiteturaCamadasTest {
    private static final String PACOTE_PORTAS =
            "br.com.pacto.recrutamento.app.ports.";
    private static final Path APP =
            Paths.get("src/main/java/br/com/pacto/recrutamento/app");
    private static final Path PORTS =
            Paths.get("src/main/java/br/com/pacto/recrutamento/app/ports");
    private static final Path CORE =
            Paths.get("src/main/java/br/com/pacto/recrutamento/core");
    private static final Path INFRA =
            Paths.get("src/main/java/br/com/pacto/recrutamento/infra");

    @Test
    void appNaoConheceInfraNemFrameworksDeIntegracao() throws IOException {
        assertThat(fontes(APP))
                .allSatisfy(fonte -> assertThat(fonte)
                        .doesNotContain(
                                "br.com.pacto.recrutamento.infra",
                                "org.springframework.data",
                                "javax.persistence",
                                "io.minio"));
    }

    @Test
    void servicesDependemDeInterfacesDefinidasEmPorts() {
        Class<?>[] services = {
                CandidatoServiceImpl.class,
                CandidaturaServiceImpl.class,
                CurriculoServiceImpl.class,
                NotificacaoServiceImpl.class,
                TemplateVagaServiceImpl.class,
                UsuarioServiceImpl.class,
                VagaServiceImpl.class
        };

        for (Class<?> service : services) {
            assertThat(Stream.of(service.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .map(field -> field.getType())
                    .filter(Class::isInterface)
                    .map(type -> type.getPackage().getName()))
                    .allMatch(pacote -> pacote.startsWith(PACOTE_PORTAS));
        }
    }

    @Test
    void portsDeSaidaSaoNomeadosComoAdapters() throws IOException {
        try (Stream<Path> arquivos = Files.walk(PORTS)) {
            assertThat(arquivos
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString()))
                    .noneMatch(nome -> nome.contains("Repository") || nome.contains("Repositorio"));
        }
    }

    @Test
    void coreNaoConheceInfraNemIntegracoesExternas() throws IOException {
        assertThat(fontes(CORE))
                .allSatisfy(fonte -> assertThat(fonte)
                        .doesNotContain(
                                "br.com.pacto.recrutamento.infra",
                                "org.springframework",
                                "io.minio"));
    }

    @Test
    void detalhesJpaFicamNaInfraSemModelosDuplicados() throws IOException {
        assertThat(fontes(INFRA))
                .anySatisfy(fonte -> assertThat(fonte)
                        .contains("JpaRepository"));

        try (Stream<Path> arquivos = Files.walk(INFRA)) {
            assertThat(arquivos
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString()))
                    .noneMatch(nome -> nome.endsWith("JpaEntity.java")
                            || nome.endsWith("JpaMapper.java")
                            || nome.endsWith("Persistido.java"));
        }
    }

    private Stream<String> fontes(Path raiz) throws IOException {
        Stream.Builder<String> fontes = Stream.builder();
        try (Stream<Path> arquivos = Files.walk(raiz)) {
            arquivos.filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> fontes.add(ler(path)));
        }
        return fontes.build();
    }

    private String ler(Path arquivo) {
        try {
            return new String(Files.readAllBytes(arquivo), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
