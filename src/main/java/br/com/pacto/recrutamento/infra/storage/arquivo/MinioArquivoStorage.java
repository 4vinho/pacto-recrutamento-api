package br.com.pacto.recrutamento.infra.storage.arquivo;

import br.com.pacto.recrutamento.app.ports.out.curriculo.ArquivoStoragePort;
import br.com.pacto.recrutamento.infra.configurations.arquivo.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http.Method;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class MinioArquivoStorage implements ArquivoStoragePort {
    private final MinioClient client;
    private final MinioClient publicClient;
    private final String bucket;

    public MinioArquivoStorage(MinioClient client,
                               @Qualifier("minioPublicClient") MinioClient publicClient,
                               MinioProperties properties) {
        this.client = client;
        this.publicClient = publicClient;
        this.bucket = properties.getBucket();
    }

    @Override
    public void armazenar(String chave, byte[] conteudo, String contentType) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(chave)
                    .contentType(contentType)
                    .stream(new ByteArrayInputStream(conteudo), (long) conteudo.length, -1L)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível armazenar o arquivo", e);
        }
    }

    @Override
    public void remover(String chave) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(chave)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível remover o arquivo", e);
        }
    }

    @Override
    public String gerarUrlTemporaria(String chave, Duration duracao) {
        try {
            return publicClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(chave)
                    .expiry(Math.toIntExact(duracao.getSeconds()), TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível gerar a URL temporária", e);
        }
    }
}
