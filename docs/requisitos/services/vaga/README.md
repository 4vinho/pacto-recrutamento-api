# Services: vaga

## Casos de uso

- `CriarVaga`, `AtualizarVaga`, `AlterarStatusVaga`, `ExcluirVaga`.
- `CriarPerguntaDaVaga`, `AtualizarPerguntaDaVaga`, `ExcluirPerguntaDaVaga`.
- `CriarRequisitoDaVaga`, `AtualizarRequisitoDaVaga`, `ExcluirRequisitoDaVaga`.

## Regras

- Apenas administrador ou papel explicitamente autorizado mantém vagas.
- Operações filhas validam que pergunta/requisito pertence à vaga recebida.
- Exclusão lógica preserva candidaturas e histórico.
- Consultas, filtros, ordenação e paginação serão definidos pelas telas reais.
