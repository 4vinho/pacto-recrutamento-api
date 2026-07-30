# Services: currículo

## Casos de uso

- `EnviarCurriculo`: valida PDF/tamanho, calcula checksum, salva o objeto e os metadados.
- `SubstituirCurriculo`: cria o novo arquivo e inativa o anterior com segurança.
- `GerarUrlTemporariaCurriculo`: autoriza o acesso e solicita URL de curta duração.

## Consistência

- Banco e object storage não compartilham transação; falha ao persistir metadados
  exige remover o novo objeto.
- Falha ao remover objeto antigo deve ser registrada para reprocessamento.
- URL proposta expira em cinco minutos e nunca é pública permanentemente.
