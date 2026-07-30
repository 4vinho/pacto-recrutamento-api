# Armazenamento de currículos

## Decisão

Os currículos serão enviados como arquivos PDF e armazenados em object storage S3-compatible.

No ambiente local, será utilizado MinIO em container. O PostgreSQL armazenará somente os metadados.

## Arquitetura

```text
Angular
   |
   | multipart/form-data
   v
API Spring Boot
   |
   | ArquivoStorage
   v
MinIO
   |
   v
Volume persistente
```

O MinIO deve ser tratado como detalhe de infraestrutura. Os casos de uso dependem da abstração `ArquivoStorage`, e não do SDK.

## Bucket

- Nome: `curriculos`.
- Acesso: privado.
- Criação automatizada na inicialização do ambiente ou por script de preparação.
- Nenhum objeto deve possuir acesso público.

## Regras do arquivo

- Apenas PDF.
- Tamanho máximo de 5 MB.
- Nome original preservado somente como metadado.
- Nome físico gerado pela aplicação.
- Chave única por candidato e currículo.
- Checksum SHA-256 calculado durante o upload.
- Um currículo ativo por candidato.
- Substituir o currículo deve inativar o anterior.

Formato da chave:

```text
curriculos/{candidatoId}/{curriculoId}.pdf
```

## Upload

Endpoint planejado:

```http
POST /candidatos/me/curriculo
Content-Type: multipart/form-data
```

Fluxo:

1. Identificar o candidato autenticado.
2. Validar presença e tamanho.
3. Validar o tipo declarado.
4. Validar a assinatura real do PDF.
5. Calcular o checksum SHA-256.
6. Gerar o identificador e a chave do objeto.
7. Enviar o arquivo ao MinIO.
8. Persistir os metadados no PostgreSQL.
9. Inativar o currículo anterior, quando existir.

O fluxo deve prever compensação: se os metadados não puderem ser persistidos, o novo objeto enviado deve ser removido do storage.

## Download

O arquivo não terá URL pública permanente.

Quando uma tela exigir o download, a API deve:

1. Validar a autorização do usuário.
2. Gerar uma URL pré-assinada de curta duração.
3. Retornar a URL temporária.

Duração inicial proposta: 5 minutos.

## Exclusão e substituição

- A exclusão dos metadados deve ser lógica.
- O objeto antigo pode ser removido fisicamente após a conclusão segura da substituição.
- Falhas na remoção devem ser registradas e reprocessadas.
- Currículos relacionados a processos em andamento devem seguir a política de retenção que ainda será definida.

## Porta da aplicação

Contrato conceitual:

```java
public interface ArquivoStorage {
    ArquivoArmazenado salvar(Arquivo arquivo);
    void excluir(String storageKey);
    String gerarUrlTemporaria(String storageKey);
}
```

Os tipos concretos devem ser definidos durante a implementação, evitando passar `MultipartFile` para `app` ou `core`, pois esse tipo pertence ao Spring Web.

## Infraestrutura local

O Docker Compose deverá conter:

- PostgreSQL.
- MinIO.
- API.
- Frontend, quando a conteinerização do frontend for realizada.

O MinIO deve possuir volume persistente. Credenciais, endpoint e nome do bucket devem ser configurados por variáveis de ambiente.

## Segurança

- Não confiar somente na extensão do arquivo.
- Não confiar somente no `Content-Type` enviado pelo cliente.
- Sanitizar o nome original antes de logs e respostas.
- Não registrar conteúdo binário.
- Não expor credenciais do MinIO ao frontend.
- Aplicar autorização tanto no upload quanto na geração da URL.
- Considerar verificação antimalware em uma evolução posterior.

## Testes planejados

- Rejeitar arquivo vazio.
- Rejeitar arquivo maior que 5 MB.
- Rejeitar conteúdo que não seja PDF.
- Armazenar PDF válido.
- Persistir metadados e checksum.
- Garantir um único currículo ativo.
- Remover o novo objeto quando a persistência falhar.
- Gerar URL temporária somente para usuário autorizado.
- Substituir o currículo sem deixar metadados inconsistentes.

## Referências

- [MinIO Object Storage for Container](https://min.io/docs/minio/container/index.html)
- [URLs pré-assinadas no Amazon S3](https://docs.aws.amazon.com/AmazonS3/latest/userguide/using-presigned-url.html)
