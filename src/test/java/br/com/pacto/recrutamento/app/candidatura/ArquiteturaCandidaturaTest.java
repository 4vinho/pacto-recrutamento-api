package br.com.pacto.recrutamento.app.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import org.junit.jupiter.api.Test;
import javax.persistence.Entity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;

class ArquiteturaCandidaturaTest {
    @Test
    void sliceDaAplicacaoNaoDependeDeSpringJpaOuInfra() throws IOException {
        Path raiz = Paths.get("src/main/java/br/com/pacto/recrutamento/app/candidatura");
        try (Stream<Path> fontes = Files.walk(raiz)) {
            fontes.filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                try {
                    String fonte = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                    assertThat(fonte).doesNotContain("org.springframework", "javax.persistence", ".infra.");
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            });
        }
        assertThat(Candidatura.class.isAnnotationPresent(Entity.class)).isFalse();
        assertThat(RespostaCandidatura.class.isAnnotationPresent(Entity.class)).isFalse();
    }
}
