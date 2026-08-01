# Currículos e notificações

## Currículos entregues

- Upload multipart de currículo PDF associado a uma candidatura.
- Substituição explícita de currículo já enviado.
- Validação de arquivo, limite de tamanho e persistência dos metadados.
- Armazenamento privado no MinIO e geração de URL temporária autorizada.
- Acesso permitido ao candidato dono ou ao responsável autorizado pela vaga.

Endpoints: `POST /candidaturas/{id}/curriculo` e
`GET /candidaturas/{id}/curriculo/url`.

## Notificações entregues

- Eventos internos para candidatura criada e status alterado.
- Identificação do candidato e dos responsáveis como destinatários.
- Persistência da notificação e listener desacoplado do caso de uso.
- Canal de entrega simulado, adequado ao escopo que aceita respostas estáticas.

## Evidências principais

`CurriculoController`, `CurriculoService`, `MinioArquivoStorage`,
`EventosCandidaturaSpringAdapter`, `EventosCandidaturaListener`,
`NotificacaoService`, `CanalNotificacaoSimulado` e migrations `V4`, `V8`, `V10`
e `V13`.
