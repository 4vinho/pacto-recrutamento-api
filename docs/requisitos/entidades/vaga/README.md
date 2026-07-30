# Entidade: vaga

## Responsabilidade

Representar uma oportunidade interna, seu responsável, perguntas e requisitos.

## Estrutura

- `vagas`: responsável, título, descrição, status e auditoria.
- `perguntas_vaga`: vaga, enunciado, tipo, obrigatoriedade, ordem e auditoria.
- `requisitos_vaga`: vaga, descrição, obrigatoriedade e auditoria.

Status: `RASCUNHO`, `PUBLICADA`, `ENCERRADA`, `CANCELADA`.

Tipos de resposta propostos: `TEXTO`, `NUMERO`, `BOOLEANO`, `DATA`,
`SELECAO_UNICA`.

## Invariantes

- Título e descrição são obrigatórios.
- Apenas vaga `PUBLICADA` recebe candidatura.
- Pergunta/requisito pertence a exatamente uma vaga; a ordem é positiva.
- Pergunta obrigatória exige resposta na candidatura.
- Alterar status usa operação própria e transições válidas.
- Exclusão é lógica e impede novas alterações.
