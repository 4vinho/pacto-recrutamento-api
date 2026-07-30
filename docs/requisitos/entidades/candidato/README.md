# Entidade: candidato

## Responsabilidade

Representar o perfil profissional associado a uma conta.

## Estrutura

Tabela `candidatos`: id, `usuario_id` único, data de admissão opcional e auditoria.

## Relacionamentos e invariantes

- `Usuario 1 --- 0..1 Candidato`.
- `Candidato 1 --- 0..N Candidatura`.
- O candidato modifica somente o próprio perfil.
- O tempo de empresa, quando usado na avaliação, deve ser derivado de uma data
  confirmada; a regra permanece pendente de validação de produto.
