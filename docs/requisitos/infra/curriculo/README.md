# Infra: currículo

- MinIO local implementa a porta `ArquivoStorage` usando protocolo S3-compatible.
- Bucket privado `curriculos`, sem acesso público e com volume persistente.
- Endpoint, credenciais e bucket vêm de variáveis de ambiente.
- Validar assinatura real do PDF, não apenas extensão ou `Content-Type`.
- Gerar URL pré-assinada de curta duração.
- PostgreSQL armazena apenas metadados e índice único parcial do currículo ativo.
- Falhas na remoção são registradas no log e não são reprocessadas.
