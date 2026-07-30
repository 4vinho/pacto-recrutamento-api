# Entidade: currículo

## Responsabilidade

Representar os metadados do PDF profissional armazenado fora do banco.

## Estrutura

Tabela `curriculos`: id, candidato, chave de storage única, nome original,
content type, tamanho, checksum SHA-256 e auditoria.

## Invariantes

- Um candidato possui no máximo um currículo ativo.
- Arquivo obrigatório, PDF real e tamanho máximo de 5 MB.
- O binário não é salvo no PostgreSQL.
- Substituição inativa o registro anterior.
- A chave proposta é `curriculos/{candidatoId}/{curriculoId}.pdf`.
