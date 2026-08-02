package br.com.pacto.recrutamento.infra.configurations.arquivo;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http.Method;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinioConfigurationTest {

    @Test
    void geraUrlPublicaSemConsultarEndpointParaDescobrirRegiao() throws Exception {
        MinioProperties properties = new MinioProperties();
        properties.setPublicEndpoint("http://localhost:9000");
        properties.setAccessKey("recrutamento");
        properties.setSecretKey("recrutamento-local");

        MinioClient client = new MinioConfiguration().minioPublicClient(properties);

        String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket("curriculos")
                .object("candidaturas/curriculo.pdf")
                .expiry(300)
                .build());

        assertThat(url).startsWith("http://localhost:9000/curriculos/candidaturas/curriculo.pdf?");
        assertThat(url).contains("X-Amz-Credential=recrutamento");
    }
}
