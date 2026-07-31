package br.com.pacto.recrutamento.infra.arquivo;

import br.com.pacto.recrutamento.app.ports.curriculo.ArquivoStorage;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MinioArquivoStorageTest {
    private final MinioClient client = mock(MinioClient.class);
    private final ArquivoStorage storage =
            new MinioArquivoStorage(client, new MinioProperties("curriculos"));

    @Test
    void armazenaNoBucketPrivadoConfigurado() throws Exception {
        byte[] conteudo = "%PDF-1.7".getBytes(StandardCharsets.UTF_8);

        storage.armazenar("candidato/curriculo.pdf", conteudo, "application/pdf");

        ArgumentCaptor<PutObjectArgs> args = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(client).putObject(args.capture());
        assertThat(args.getValue().bucket()).isEqualTo("curriculos");
        assertThat(args.getValue().object()).isEqualTo("candidato/curriculo.pdf");
        assertThat(args.getValue().contentType().toString()).isEqualTo("application/pdf");
    }

    @Test
    void removeObjetoDoBucketConfigurado() throws Exception {
        storage.remover("candidato/antigo.pdf");

        ArgumentCaptor<RemoveObjectArgs> args = ArgumentCaptor.forClass(RemoveObjectArgs.class);
        verify(client).removeObject(args.capture());
        assertThat(args.getValue().bucket()).isEqualTo("curriculos");
        assertThat(args.getValue().object()).isEqualTo("candidato/antigo.pdf");
    }

    @Test
    void delegaGeracaoDeUrlPreAssinada() throws Exception {
        when(client.getPresignedObjectUrl(
                org.mockito.ArgumentMatchers.any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("https://minio/url-assinada");

        String url = storage.gerarUrlTemporaria(
                "candidato/curriculo.pdf", Duration.ofMinutes(5));

        assertThat(url).isEqualTo("https://minio/url-assinada");
        ArgumentCaptor<GetPresignedObjectUrlArgs> args =
                ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
        verify(client).getPresignedObjectUrl(args.capture());
        assertThat(args.getValue().bucket()).isEqualTo("curriculos");
        assertThat(args.getValue().object()).isEqualTo("candidato/curriculo.pdf");
        assertThat(args.getValue().expiry()).isEqualTo(300);
    }
}
