# Decisões pendentes

## Objetivo

Registrar as perguntas que precisam ser respondidas antes ou durante a implementação.

## Prioridade alta

### Identificadores

Proposta: utilizar UUID em todas as entidades.

Confirmar:

- O UUID será aceito?
- Ele será gerado pela aplicação ou pelo PostgreSQL?

Recomendação: geração pela aplicação, permitindo que o domínio possua identidade antes da persistência.

### Duração do refresh token

O access token já foi definido com duração de 15 minutos.

Proposta inicial:

- Refresh token válido por 7 dias.
- Renovação rotativa a cada uso.
- Revogação do token anterior após a renovação.

Confirmar a duração desejada.

### Sessões simultâneas

Confirmar se um usuário poderá permanecer autenticado em mais de um dispositivo.

Recomendação: permitir múltiplas sessões, mantendo um refresh token independente por dispositivo.

### Cadastro de usuários

Confirmar se:

- Qualquer colaborador poderá se cadastrar.
- Apenas administradores poderão criar usuários.
- Haverá validação por domínio de e-mail corporativo.

### Criação do perfil de candidato

Confirmar se o perfil será:

- Criado automaticamente no cadastro do usuário; ou
- Criado posteriormente pelo comando `CriarCandidato`.

Proposta atual: criar posteriormente, mantendo conta e perfil profissional como conceitos separados.

### Currículo

Decisão confirmada:

- Upload de arquivo PDF.
- Limite de 5 MB.
- Arquivo armazenado em bucket privado no MinIO.
- MinIO executado em container com volume persistente no ambiente local.
- Metadados armazenados no PostgreSQL.
- Upload recebido e validado pela API.
- Download disponibilizado por URL temporária.
- Um currículo ativo por candidato.
- Integração isolada pela porta `ArquivoStorage`.

### Responsável pela vaga

Confirmar se uma vaga terá:

- Exatamente um responsável; ou
- Vários responsáveis.

Proposta atual: exatamente um usuário com papel `RESPONSAVEL_VAGA`.

### Respostas

Já definido:

- O endpoint aceita uma ou mais respostas.
- A coleção não pode estar vazia.
- O lote é processado em uma única transação.

Ainda é necessário confirmar:

- Uma resposta existente poderá ser alterada?
- Até qual status da candidatura as respostas poderão ser modificadas?

### Notificações

Confirmar o canal inicial:

- E-mail.
- Notificação interna.
- Ambos.

Proposta para o MVP: notificação interna persistida; envio de e-mail pode ser simulado.

## Prioridade média

- Definir se os templates entram no primeiro MVP.
- Definir os tipos de pergunta aceitos.
- Definir como requisitos objetivos serão avaliados.
- Definir se vagas encerradas podem ser reabertas.
- Definir as transições permitidas entre os status da candidatura.
- Definir a política de retenção dos registros excluídos logicamente.
